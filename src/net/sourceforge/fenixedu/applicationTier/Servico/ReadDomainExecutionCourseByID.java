/**
 * 
 */
package net.sourceforge.fenixedu.applicationTier.Servico;

import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.NonExistingServiceException;
import net.sourceforge.fenixedu.domain.ExecutionCourse;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;
import net.sourceforge.fenixedu.persistenceTier.ISuportePersistente;
import net.sourceforge.fenixedu.persistenceTier.PersistenceSupportFactory;
import net.sourceforge.fenixedu.applicationTier.IService;

/**
 * @author <a href="mailto:goncalo@ist.utl.pt">Goncalo Luiz</a> <br/> <br/>
 *         <br/> Created on 19:48:00,26/Set/2005
 * @version $Id: ReadDomainExecutionCourseByID.java,v 1.2 2005/12/16 16:05:59
 *          lepc Exp $
 */
public class ReadDomainExecutionCourseByID implements IService {
    public ExecutionCourse run(Integer idInternal) throws FenixServiceException, ExcepcaoPersistencia {

        ExecutionCourse executionCourse = null;
        ISuportePersistente sp = PersistenceSupportFactory.getDefaultPersistenceSupport();
        executionCourse = (ExecutionCourse) sp.getIPersistentExecutionCourse().readByOID(
                ExecutionCourse.class, idInternal);

        if (executionCourse == null) {
            throw new NonExistingServiceException();
        }

        return executionCourse;
    }
}
