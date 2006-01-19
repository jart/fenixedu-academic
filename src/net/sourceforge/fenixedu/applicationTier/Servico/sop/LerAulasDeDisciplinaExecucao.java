/*
 * Created on 2005/05/11
 * 
 */
package net.sourceforge.fenixedu.applicationTier.Servico.sop;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.fenixedu.dataTransferObject.InfoExecutionCourse;
import net.sourceforge.fenixedu.dataTransferObject.InfoLesson;
import net.sourceforge.fenixedu.dataTransferObject.InfoRoom;
import net.sourceforge.fenixedu.dataTransferObject.InfoShift;
import net.sourceforge.fenixedu.domain.ExecutionCourse;
import net.sourceforge.fenixedu.domain.Lesson;
import net.sourceforge.fenixedu.domain.Shift;
import net.sourceforge.fenixedu.domain.space.Room;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;
import net.sourceforge.fenixedu.persistenceTier.IPersistentExecutionCourse;
import net.sourceforge.fenixedu.persistenceTier.ISuportePersistente;
import net.sourceforge.fenixedu.persistenceTier.PersistenceSupportFactory;
import net.sourceforge.fenixedu.applicationTier.IService;

/**
 * 
 * @author Luis Cruz
 */
public class LerAulasDeDisciplinaExecucao implements IService {

    public Object run(final InfoExecutionCourse infoExecutionCourse) throws ExcepcaoPersistencia {
        final ISuportePersistente persistentSupport = PersistenceSupportFactory.getDefaultPersistenceSupport();
        final IPersistentExecutionCourse persistentExecutionCourse = persistentSupport.getIPersistentExecutionCourse();

        final ExecutionCourse executionCourse = (ExecutionCourse) persistentExecutionCourse.readByOID(ExecutionCourse.class, infoExecutionCourse.getIdInternal());
        final List<Shift> shifts = executionCourse.getAssociatedShifts();

        // An estimated upper bound for the number of elements is three lessons per shift.
        final int estimatedNumberOfLessons = shifts.size() * 3;

        final List<InfoLesson> infoLessons = new ArrayList<InfoLesson>(estimatedNumberOfLessons);

        for (final Shift shift : shifts) {
            final InfoShift infoShift = InfoShift.newInfoFromDomain(shift);

            final List<Lesson> lessons = shift.getAssociatedLessons();
            for (final Lesson lesson : lessons) {
                //final InfoLesson infoLesson = 
                //        InfoLessonWithInfoRoomAndInfoRoomOccupationAndInfoPeriod.newInfoFromDomain(lesson);
                final InfoLesson infoLesson = InfoLesson.newInfoFromDomain(lesson);
                infoLessons.add(infoLesson);

                infoLesson.setInfoShift(infoShift);

                final Room room = lesson.getSala();
                final InfoRoom infoRoom = InfoRoom.newInfoFromDomain(room);
                infoLesson.setInfoSala(infoRoom);
            }
        }

        return infoLessons;
    }

}