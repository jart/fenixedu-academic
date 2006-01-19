/*
 * Created on 14/Nov/2003
 *  
 */
package net.sourceforge.fenixedu.applicationTier.Servico.framework;

import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.ExistingServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.NonExistingServiceException;
import net.sourceforge.fenixedu.dataTransferObject.InfoObject;
import net.sourceforge.fenixedu.domain.DomainObject;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;
import net.sourceforge.fenixedu.persistenceTier.IPersistentObject;
import net.sourceforge.fenixedu.persistenceTier.ISuportePersistente;
import net.sourceforge.fenixedu.persistenceTier.PersistenceSupportFactory;
import net.sourceforge.fenixedu.applicationTier.IService;

/**
 * @author Leonor Almeida
 * @author Sergio Montelobo
 * @author jpvl
 * @author Barbosa
 */
public abstract class EditDomainObjectService implements IService {

	/**
	 * Checks if the objectToEdit can be created or update
	 * 
	 * @param objectToEdit
	 * @param existingDomainObject
	 * @return
	 */
	private boolean canCreate(InfoObject objectToEdit, DomainObject existingDomainObject) {
		/*
		 * Not new and exist on database and object ids are equal OR is a new
		 * object and doesn't exist on database OR is not new and doesn't exist
		 * on database (unique changed)
		 */

		return (!isNew(objectToEdit)
				&& ((existingDomainObject != null) && (objectToEdit.getIdInternal()
						.equals(existingDomainObject.getIdInternal()))) || ((existingDomainObject == null)));
	}

	/**
	 * By default this method does nothing
	 * 
	 * @param newDomainObject
	 * @param infoObject
	 * @param sp
	 */
	protected void doAfterLock(DomainObject domainObjectLocked, InfoObject infoObject,
			ISuportePersistente sp) throws Exception {
	}

	/**
	 * By default this method does nothing
	 * 
	 * @param objectLocked
	 * @param infoObject
	 * @param sp
	 */
	protected void doBeforeLock(DomainObject domainObjectToLock, InfoObject infoObject,
			ISuportePersistente sp) throws Exception {
	}

	/**
	 * @param sp
	 * @return
	 */
	protected abstract IPersistentObject getIPersistentObject(ISuportePersistente sp)
			throws ExcepcaoPersistencia;

	/**
	 * Checks if the internalId of the object is null or 0
	 * 
	 * @param domainObject
	 * @return
	 */
	protected boolean isNew(InfoObject domainObject)

	{
		Integer objectId = domainObject.getIdInternal();
		return ((objectId == null) || objectId.equals(new Integer(0)));
	}

	/**
	 * This method invokes a persistent method to read an DomainObject from
	 * database
	 * 
	 * @param domainObject
	 * @return By default returns null. When there is no unique in domainObject
	 *         the object that we want to create never exists.
	 */
	protected DomainObject readObjectByUnique(InfoObject infoObject, ISuportePersistente sp)
			throws Exception {
		return null;
	}

	/**
	 * Executes the service
	 * 
	 * @param objectId
	 * @param infoObject
	 * @return
	 * @throws Exception
	 */
	public void run(Integer objectId, InfoObject infoObject) throws Exception {
		ISuportePersistente sp = PersistenceSupportFactory.getDefaultPersistenceSupport();

		DomainObject objectFromDatabase = getObjectFromDatabase(infoObject, sp);
		if (!canCreate(infoObject, objectFromDatabase)) {
			throw new ExistingServiceException("The object already exists");
		}
		DomainObject domainObject = getObjectToLock(infoObject, objectFromDatabase);

		doBeforeLock(domainObject, infoObject, sp);

		copyInformationFromInfoToDomain(sp, infoObject, domainObject);

		doAfterLock(domainObject, infoObject, sp);
	}

	private DomainObject getObjectToLock(InfoObject infoObject, DomainObject objectFromDatabase)
			throws InstantiationException, IllegalAccessException {
		DomainObject domainObject = null;

		if (isNew(infoObject)) {
			domainObject = createNewDomainObject(infoObject);
		} else {
			domainObject = objectFromDatabase;
		}
		return domainObject;
	}

	protected abstract DomainObject createNewDomainObject(InfoObject infoObject);

	protected abstract Class getDomainObjectClass();

	protected abstract void copyInformationFromInfoToDomain(ISuportePersistente sp,
			InfoObject infoObject, DomainObject domainObject) throws ExcepcaoPersistencia,
			FenixServiceException;

	private DomainObject getObjectFromDatabase(InfoObject infoObject, ISuportePersistente sp)
			throws Exception {
		DomainObject objectFromDatabase = readObjectByUnique(infoObject, sp);

		// if the editing means alter unique keys or the there is no unique
		// then read by oid to get the object from database.
		if (objectFromDatabase == null && !isNew(infoObject)) {
			objectFromDatabase = getIPersistentObject(sp).readByOID(getDomainObjectClass(),
					infoObject.getIdInternal());
			// if the object still null then the object doesn't exist.
			if (objectFromDatabase == null) {
				throw new NonExistingServiceException("Object doesn't exist!");
			}
		}
		return objectFromDatabase;
	}
}