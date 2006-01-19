package net.sourceforge.fenixedu.applicationTier.Servico.masterDegree.administrativeOffice.student.studentCurricularPlan;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.fenixedu.applicationTier.IUserView;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.InvalidArgumentsServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.manager.DeleteEnrollment;
import net.sourceforge.fenixedu.domain.Attends;
import net.sourceforge.fenixedu.domain.Branch;
import net.sourceforge.fenixedu.domain.CreditsInAnySecundaryArea;
import net.sourceforge.fenixedu.domain.CreditsInScientificArea;
import net.sourceforge.fenixedu.domain.DomainFactory;
import net.sourceforge.fenixedu.domain.Employee;
import net.sourceforge.fenixedu.domain.Enrolment;
import net.sourceforge.fenixedu.domain.EnrolmentEquivalence;
import net.sourceforge.fenixedu.domain.EnrolmentEvaluation;
import net.sourceforge.fenixedu.domain.EnrolmentInExtraCurricularCourse;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.StudentCurricularPlan;
import net.sourceforge.fenixedu.domain.curriculum.EnrollmentState;
import net.sourceforge.fenixedu.domain.studentCurricularPlan.Specialization;
import net.sourceforge.fenixedu.domain.studentCurricularPlan.StudentCurricularPlanState;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;
import net.sourceforge.fenixedu.persistenceTier.IPersistentBranch;
import net.sourceforge.fenixedu.persistenceTier.IPersistentEmployee;
import net.sourceforge.fenixedu.persistenceTier.IPersistentEnrollment;
import net.sourceforge.fenixedu.persistenceTier.IPersistentStudentCurricularPlan;
import net.sourceforge.fenixedu.persistenceTier.IPessoaPersistente;
import net.sourceforge.fenixedu.persistenceTier.ISuportePersistente;
import net.sourceforge.fenixedu.persistenceTier.PersistenceSupportFactory;
import net.sourceforge.fenixedu.applicationTier.IService;

/**
 * @author Jo�o Mota 15/Out/2003
 */

public class EditPosGradStudentCurricularPlanStateAndCredits implements IService {

	public void run(IUserView userView, Integer studentCurricularPlanId, String currentState,
			Double credits, String startDate, List extraCurricularCourses, String observations,
			Integer branchId, String specialization) throws FenixServiceException, ExcepcaoPersistencia {

		ISuportePersistente sp = PersistenceSupportFactory.getDefaultPersistenceSupport();

		IPersistentStudentCurricularPlan persistentStudentCurricularPlan = sp
				.getIStudentCurricularPlanPersistente();

		StudentCurricularPlan studentCurricularPlan = (StudentCurricularPlan) persistentStudentCurricularPlan
				.readByOID(StudentCurricularPlan.class, studentCurricularPlanId);
		if (studentCurricularPlan == null) {
			throw new InvalidArgumentsServiceException();
		}

		StudentCurricularPlanState newState = StudentCurricularPlanState.valueOf(currentState);

		Employee employee = null;
		IPersistentEmployee persistentEmployee = sp.getIPersistentEmployee();
		IPessoaPersistente persistentPerson = sp.getIPessoaPersistente();
		IPersistentEnrollment persistentEnrolment = sp.getIPersistentEnrolment();
		IPersistentBranch persistentBranch = sp.getIPersistentBranch();

		Person person = persistentPerson.lerPessoaPorUsername(userView.getUtilizador());
		if (person == null) {
			throw new InvalidArgumentsServiceException();
		}

		employee = persistentEmployee.readByPerson(person.getIdInternal().intValue());
		if (employee == null) {
			throw new InvalidArgumentsServiceException();
		}

		Branch branch = (Branch) persistentBranch.readByOID(Branch.class, branchId);
		if (branch == null) {
			throw new InvalidArgumentsServiceException();
		}

		studentCurricularPlan.setStartDate(stringDateToCalendar(startDate).getTime());
		studentCurricularPlan.setCurrentState(newState);
		studentCurricularPlan.setEmployee(employee);
		studentCurricularPlan.setGivenCredits(credits);
		studentCurricularPlan.setObservations(observations);
		studentCurricularPlan.setBranch(branch);
		studentCurricularPlan.setSpecialization(Specialization.valueOf(specialization));

		List enrollments = studentCurricularPlan.getEnrolments();
		Iterator iterator = enrollments.iterator();
		if (newState.equals(StudentCurricularPlanState.INACTIVE)) {
			while (iterator.hasNext()) {
				Enrolment enrolment = (Enrolment) iterator.next();
				if (enrolment.getEnrollmentState() == EnrollmentState.ENROLLED
						|| enrolment.getEnrollmentState() == EnrollmentState.TEMPORARILY_ENROLLED) {
					enrolment.setEnrollmentState(EnrollmentState.ANNULED);
				}
			}
		} else {

			while (iterator.hasNext()) {
				Enrolment enrolment = (Enrolment) iterator.next();
				if (extraCurricularCourses.contains(enrolment.getIdInternal())) {
					if (!(enrolment instanceof EnrolmentInExtraCurricularCourse)) {

						Enrolment auxEnrolment = DomainFactory.makeEnrolmentInExtraCurricularCourse();

						copyEnrollment(enrolment, auxEnrolment);
						setEnrolmentCreationInformation(userView, auxEnrolment);

						changeAnnulled2ActiveIfActivePlan(newState, persistentEnrolment, auxEnrolment);

						enrolment.delete();
					} else {
						changeAnnulled2ActiveIfActivePlan(newState, persistentEnrolment, enrolment);
					}
				} else {
					if (enrolment instanceof EnrolmentInExtraCurricularCourse) {

						Enrolment auxEnrolment = DomainFactory.makeEnrolment();

						copyEnrollment(enrolment, auxEnrolment);
						setEnrolmentCreationInformation(userView, auxEnrolment);

						changeAnnulled2ActiveIfActivePlan(newState, persistentEnrolment, auxEnrolment);

						DeleteEnrollment deleteEnrolmentService = new DeleteEnrollment();
						deleteEnrolmentService.run(enrolment.getIdInternal());
					} else {
						changeAnnulled2ActiveIfActivePlan(newState, persistentEnrolment, enrolment);
					}
				}
			}
		}

	}

	private void setEnrolmentCreationInformation(IUserView userView, Enrolment auxEnrolment) {
		auxEnrolment.setCreationDate(Calendar.getInstance().getTime());
		auxEnrolment.setCreatedBy(userView.getUtilizador());
	}

	/**
	 * @param enrolment
	 * @param auxEnrolment
	 * @throws FenixServiceException
	 */
	private void copyEnrollment(Enrolment enrolment, Enrolment auxEnrolment) {

		auxEnrolment.setCreatedBy(enrolment.getCreatedBy());
		auxEnrolment.setCreationDate(enrolment.getCreationDate());
		auxEnrolment.setAccumulatedWeight(enrolment.getAccumulatedWeight());
		auxEnrolment.setCurricularCourse(enrolment.getCurricularCourse());
		auxEnrolment.setExecutionPeriod(enrolment.getExecutionPeriod());
		auxEnrolment.setStudentCurricularPlan(enrolment.getStudentCurricularPlan());
		auxEnrolment.setCondition(enrolment.getCondition());
		auxEnrolment.setEnrolmentEvaluationType(enrolment.getEnrolmentEvaluationType());
		auxEnrolment.setEnrollmentState(enrolment.getEnrollmentState());

		for (final List<EnrolmentEvaluation> evaluations = enrolment.getEvaluations(); !evaluations
				.isEmpty(); auxEnrolment.addEvaluations(evaluations.get(0)))
			;
		for (final List<EnrolmentEquivalence> equivalences = enrolment.getEnrolmentEquivalences(); !equivalences
				.isEmpty(); auxEnrolment.addEnrolmentEquivalences(equivalences.get(0)))
			;
		for (final List<Attends> attends = enrolment.getAttends(); !attends.isEmpty(); auxEnrolment
				.addAttends(attends.get(0)))
			;
		for (final List<CreditsInAnySecundaryArea> credits = enrolment.getCreditsInAnySecundaryAreas(); !credits
				.isEmpty(); auxEnrolment.addCreditsInAnySecundaryAreas(credits.get(0)))
			;
		for (final List<CreditsInScientificArea> credits = enrolment.getCreditsInScientificAreas(); !credits
				.isEmpty(); auxEnrolment.addCreditsInScientificAreas(credits.get(0)))
			;

	}

	/**
	 * @param newState
	 * @param persistentEnrolment
	 * @param enrolment
	 * @throws ExcepcaoPersistencia
	 */
	private void changeAnnulled2ActiveIfActivePlan(StudentCurricularPlanState newState,
			IPersistentEnrollment persistentEnrolment, Enrolment enrolment) throws ExcepcaoPersistencia {
		if (newState.equals(StudentCurricularPlanState.ACTIVE)) {
			if (enrolment.getEnrollmentState() == EnrollmentState.ANNULED) {
				enrolment.setEnrollmentState(EnrollmentState.ENROLLED);
			}
		}
	}

	private Calendar stringDateToCalendar(String startDate) throws NumberFormatException {
		Calendar calendar = Calendar.getInstance();
		String[] aux = startDate.split("/");
		calendar.set(Calendar.DAY_OF_MONTH, (new Integer(aux[0])).intValue());
		calendar.set(Calendar.MONTH, (new Integer(aux[1])).intValue() - 1);
		calendar.set(Calendar.YEAR, (new Integer(aux[2])).intValue());

		return calendar;
	}

}
