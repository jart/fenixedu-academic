/*
 * Created on 1/Jun/2005 - 13:26:47
 * 
 */

package net.sourceforge.fenixedu.applicationTier.Servico.student;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.dataTransferObject.InfoFrequenta;
import net.sourceforge.fenixedu.dataTransferObject.InfoStudentWithAttendsAndInquiriesRegistries;
import net.sourceforge.fenixedu.dataTransferObject.inquiries.InfoInquiriesRegistry;
import net.sourceforge.fenixedu.domain.Attends;
import net.sourceforge.fenixedu.domain.Student;
import net.sourceforge.fenixedu.domain.inquiries.InquiriesRegistry;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;
import net.sourceforge.fenixedu.persistenceTier.IFrequentaPersistente;
import net.sourceforge.fenixedu.persistenceTier.ISuportePersistente;
import net.sourceforge.fenixedu.persistenceTier.PersistenceSupportFactory;
import net.sourceforge.fenixedu.applicationTier.IService;

/**
 * @author Jo�o Fialho & Rita Ferreira
 *
 */
public class ReadStudentsWithAttendsByDegreeCurricularPlanAndExecutionPeriod
		implements IService {
	
	public List<InfoStudentWithAttendsAndInquiriesRegistries> run(final Integer degreeCurricularPlanId, final Integer executionPeriodId, final Boolean onlyAttendsWithTeachers)
	throws ExcepcaoPersistencia, FenixServiceException {

		if(degreeCurricularPlanId == null) {
            throw new FenixServiceException("nullDegreeCurricularPlanId");
		}
		if(executionPeriodId == null) {
            throw new FenixServiceException("nullExecutionPeriodId");
		}
		if(onlyAttendsWithTeachers == null) {
            throw new FenixServiceException("nullOnlyAttendsWithTeachers");
		}

        final ISuportePersistente sp = PersistenceSupportFactory.getDefaultPersistenceSupport();
        final IFrequentaPersistente persistentAttend = sp.getIFrequentaPersistente();
		
		List<Attends> attendsList =
			persistentAttend.readByDegreeCurricularPlanAndExecutionPeriodOrderedByStudentId(degreeCurricularPlanId, executionPeriodId);
		
		//FIXME: It only concerns enrolled courses
		List<InfoStudentWithAttendsAndInquiriesRegistries> res = new ArrayList<InfoStudentWithAttendsAndInquiriesRegistries>();
		InfoStudentWithAttendsAndInquiriesRegistries currentStudent = null;
		for(Attends attends : attendsList) {
			
			if((!onlyAttendsWithTeachers) || 
					((!attends.getDisciplinaExecucao().getProfessorships().isEmpty()) || 
							(!attends.getDisciplinaExecucao().getNonAffiliatedTeachers().isEmpty()))) {
				
				InfoFrequenta infoAttends = InfoFrequenta.newInfoFromDomain(attends);
				if(!infoAttends.getAluno().equals(currentStudent)) {
					currentStudent = new InfoStudentWithAttendsAndInquiriesRegistries(infoAttends.getAluno());
					currentStudent.setAttends(new ArrayList<InfoFrequenta>());
					copyStudentInquiriesRegistries(currentStudent, attends.getAluno());
					res.add(currentStudent);
				}
				currentStudent.getAttends().add(infoAttends);
				
			
			}
			
		}
		

		return res;
	}

	private void copyStudentInquiriesRegistries(InfoStudentWithAttendsAndInquiriesRegistries currentStudent, Student aluno) {
		
		List<InquiriesRegistry> inquiriesRegistries = aluno.getAssociatedInquiriesRegistries();
		List<InfoInquiriesRegistry> infoRegistries = new ArrayList<InfoInquiriesRegistry>(inquiriesRegistries.size());
		
		for(InquiriesRegistry reg : inquiriesRegistries) {
			try {
				infoRegistries.add(InfoInquiriesRegistry.newInfoFromDomain(reg));
				
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		currentStudent.setInquiriesRegistries(infoRegistries);
	}
			

}
