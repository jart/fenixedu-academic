package net.sourceforge.fenixedu.applicationTier.Servico.manager;

import java.util.List;

import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.InvalidArgumentsServiceException;
import net.sourceforge.fenixedu.dataTransferObject.InfoDegree;
import net.sourceforge.fenixedu.domain.Degree;
import net.sourceforge.fenixedu.domain.DegreeCurricularPlan;
import net.sourceforge.fenixedu.domain.DomainFactory;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;
import net.sourceforge.fenixedu.persistenceTier.ISuportePersistente;
import net.sourceforge.fenixedu.persistenceTier.PersistenceSupportFactory;
import net.sourceforge.fenixedu.applicationTier.IService;

public class InsertDegree implements IService {

    public void run(InfoDegree infoDegree) throws ExcepcaoPersistencia, FenixServiceException {
        if (infoDegree.getNome() == null || infoDegree.getNameEn() == null
                || infoDegree.getSigla() == null || infoDegree.getTipoCurso() == null) {
            throw new InvalidArgumentsServiceException();
        }

        final ISuportePersistente persistentSupport = PersistenceSupportFactory
                .getDefaultPersistenceSupport();
        final List<Degree> degrees = (List<Degree>) persistentSupport.getICursoPersistente()
                .readAllFromOldDegreeStructure();
                
        // assert unique degree code and unique pair name/type
        for (Degree degree : degrees) {
            if (degree.getSigla().equalsIgnoreCase(infoDegree.getSigla())) {
                throw new FenixServiceException("error.existing.code");
            }
            if ((degree.getNome().equalsIgnoreCase(infoDegree.getNome()) || degree.getNameEn()
                    .equalsIgnoreCase(infoDegree.getNameEn()))
                    && degree.getTipoCurso().equals(infoDegree.getTipoCurso())) {
                throw new FenixServiceException("error.existing.name.and.type");
            }
        }

        DomainFactory.makeDegree(infoDegree.getNome(), infoDegree.getNameEn(),
				infoDegree.getSigla(), infoDegree.getTipoCurso(), infoDegree.getGradeScale(), DegreeCurricularPlan.class.getName());
    }

}
