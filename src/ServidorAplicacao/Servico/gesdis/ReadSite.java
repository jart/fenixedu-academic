package ServidorAplicacao.Servico.gesdis;

/**
 * Servi�o ObterSitio. Obtem a informacao sobre um sitio.
 *
 * @author Joao Pereira
 * @version
 **/

import DataBeans.InfoExecutionCourse;
import DataBeans.gesdis.InfoSite;
import DataBeans.util.Cloner;
import Dominio.IDisciplinaExecucao;
import Dominio.ISite;
import ServidorAplicacao.FenixServiceException;
import ServidorAplicacao.IServico;
import ServidorPersistente.ExcepcaoPersistencia;
import ServidorPersistente.ISuportePersistente;
import ServidorPersistente.OJB.SuportePersistenteOJB;

public class ReadSite implements IServico {

  private static ReadSite service = new ReadSite();

  /**
   * The singleton access method of this class.
   **/
  public static ReadSite getService() {
    return service;
  }

  /**
   * The ctor of this class.
   **/
  private ReadSite() {
  }

  /**
   * Returns the name of this service.
   **/
  public final String getNome() {
    return "ReadSite";
  }

  /**
   * Executes the service. Returns the all information about the
   * desired sitio.
   *
   * @param infoExecutionCourse is the infoExecutionCourse of the desired site.
   *
   * @throws ExcepcaoInexistente if there is none sitio with the
   * desired name.
   **/
  public InfoSite run(InfoExecutionCourse infoExecutionCourse)
    throws FenixServiceException {
    try {
		ISite site = null;
		ISuportePersistente sp;
		IDisciplinaExecucao executionCourse = Cloner.copyInfoExecutionCourse2ExecutionCourse(infoExecutionCourse);
		sp = SuportePersistenteOJB.getInstance();
		site = sp.getIPersistentSite().readByExecutionCourse(executionCourse);
		InfoSite infoSite = null;
		
		if (site !=null) {
			infoSite=Cloner.copyISite2InfoSite(site);
		}
		

		
		return infoSite;
	} catch (ExcepcaoPersistencia e) {
		throw new FenixServiceException(e);
	} 
  }


}
