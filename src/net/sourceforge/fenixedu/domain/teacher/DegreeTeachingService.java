package net.sourceforge.fenixedu.domain.teacher;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.sourceforge.fenixedu.domain.ExecutionCourse;
import net.sourceforge.fenixedu.domain.ExecutionSemester;
import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.Lesson;
import net.sourceforge.fenixedu.domain.Professorship;
import net.sourceforge.fenixedu.domain.Shift;
import net.sourceforge.fenixedu.domain.ShiftType;
import net.sourceforge.fenixedu.domain.Teacher;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.person.RoleType;
import net.sourceforge.fenixedu.util.CalendarUtil;
import net.sourceforge.fenixedu.util.DiaSemana;
import net.sourceforge.fenixedu.util.WeekDay;

public class DegreeTeachingService extends DegreeTeachingService_Base {

    public static final Comparator<DegreeTeachingService> DEGREE_TEACHING_SERVICE_COMPARATOR_BY_SHIFT = new Comparator<DegreeTeachingService>() {
	public int compare(DegreeTeachingService degreeTeachingService1, DegreeTeachingService degreeTeachingService2) {
	    return Shift.SHIFT_COMPARATOR_BY_TYPE_AND_ORDERED_LESSONS.compare(degreeTeachingService1.getShift(),
		    degreeTeachingService2.getShift());
	}
    };

    public DegreeTeachingService(TeacherService teacherService, Professorship professorship, Shift shift, Double percentage,
	    RoleType roleType) {

	super();
	if (teacherService == null || professorship == null || shift == null || percentage == null) {
	    throw new DomainException("arguments can't be null");
	}
	if (percentage > 100 || percentage < 0) {
	    throw new DomainException("message.invalid.professorship.percentage");
	}
	if (professorship.getExecutionCourse().isMasterDegreeDFAOrDEAOnly()) {
	    throw new DomainException("message.invalid.executionCourse");
	}

	setTeacherService(teacherService);
	getTeacherService().getExecutionPeriod().checkValidCreditsPeriod(roleType);
	setProfessorship(professorship);
	setShift(shift);

	Double availablePercentage = getShift().getAvailableShiftPercentage(getProfessorship());

	if (percentage > availablePercentage) {
	    throw new DomainException("message.exceeded.professorship.percentage");
	}

	if (percentage == 100) {
	    verifyAnyOverLapPeriod();
	}

	setPercentage(percentage);
    }

    @Override
    public void delete() {
	removeTeacherService();
	removeShift();
	removeProfessorship();
	super.delete();
    }

    public void delete(RoleType roleType) {
	getTeacherService().getExecutionPeriod().checkValidCreditsPeriod(roleType);
	delete();
    }

    public void updatePercentage(Double percentage, RoleType roleType) {
	getTeacherService().getExecutionPeriod().checkValidCreditsPeriod(roleType);
	if (percentage == null || percentage > 100 || percentage < 0) {
	    throw new DomainException("message.invalid.professorship.percentage");
	}
	if (percentage == 0) {
	    delete(roleType);
	} else {
	    Double availablePercentage = getShift().getAvailableShiftPercentage(getProfessorship());
	    if (percentage > availablePercentage) {
		throw new DomainException("message.exceeded.professorship.percentage");
	    }
	    if (percentage == 100) {
		verifyAnyOverLapPeriod();
	    }
	    setPercentage(percentage);
	}
    }

    private void verifyAnyOverLapPeriod() {
	verifyOverlapLessonPeriods();
	for (Lesson lesson : getShift().getAssociatedLessons()) {
	    WeekDay lessonWeekDay = WeekDay.getWeekDay(lesson.getDiaSemana());
	    Date lessonStart = lesson.getBegin();
	    Date lessonEnd = lesson.getEnd();
	    getTeacherService().verifyOverlappingWithInstitutionWorkingTime(lessonStart, lessonEnd, lessonWeekDay);
	    getTeacherService().verifyOverlappingWithSupportLesson(lessonStart, lessonEnd, lessonWeekDay);
	}
    }

    // TODO verify with other teachingServices
    private void verifyOverlapLessonPeriods() {
	List<Lesson> lessons = getShift().getAssociatedLessons();
	for (Lesson lesson : lessons) {
	    DiaSemana lessonWeekDay = lesson.getDiaSemana();
	    Date lessonStart = lesson.getBegin();
	    Date lessonEnd = lesson.getEnd();
	    int fromIndex = lessons.indexOf(lesson) + 1;
	    int toIndex = lessons.size();
	    for (Lesson otherLesson : lessons.subList(fromIndex, toIndex)) {
		if (otherLesson.getDiaSemana().equals(lessonWeekDay)) {
		    Date otherStart = otherLesson.getBegin();
		    Date otherEnd = otherLesson.getEnd();
		    if (CalendarUtil.intersectTimes(lessonStart, lessonEnd, otherStart, otherEnd)) {
			throw new DomainException("message.overlapping.lesson.period");
		    }
		}
	    }
	}
    }

    public double calculateCredits() {
	double credits = 0;
	final ExecutionCourse executionCourse = getProfessorship().getExecutionCourse();
	final ExecutionSemester executionSemester = executionCourse.getExecutionPeriod();
	ExecutionYear executionYear = ExecutionYear.readStartExecutionYearForOptionalCurricularCoursesWithLessTenEnrolments();
	if (!executionCourse.isMasterDegreeDFAOrDEAOnly()
		&& (executionSemester.getExecutionYear().isBefore(executionYear) || !executionCourse
			.areAllOptionalCurricularCoursesWithLessTenEnrolments())) {
	    final Teacher teacher = getProfessorship().getTeacher();
	    final Category teacherCategory = teacher.getCategoryForCreditsByPeriod(executionSemester);
	    if (teacherCategory != null
		    && ((teacherCategory.getCode().equals("AST") && teacherCategory.getLongName().equals("ASSISTENTE")) || (teacherCategory
			    .getCode().equals("ASC") && teacherCategory.getLongName().equals("ASSISTENTE CONVIDADO")))
		    && getShift().containsType(ShiftType.TEORICA)) {
		double hours = getShift().getUnitHours().doubleValue();
		credits += (hours * (getPercentage().doubleValue() / 100)) * 1.5;
	    } else {
		double hoursAfter20PM = getShift().hoursAfter(20);
		double hoursBefore20PM = getShift().getUnitHours().doubleValue() - hoursAfter20PM;
		credits += hoursBefore20PM * (getPercentage().doubleValue() / 100);
		credits += (hoursAfter20PM * (getPercentage().doubleValue() / 100)) * 1.5;
	    }
	}
	return credits;
    }

}
