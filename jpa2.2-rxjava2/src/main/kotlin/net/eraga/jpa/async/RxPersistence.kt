package net.eraga.jpa.async

import io.reactivex.Single
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence
import javax.persistence.PersistenceException
import javax.persistence.PersistenceUtil

/**
 * RxJava2 wrapper for [Persistence] class
 */
object RxPersistence {
    /**
     * Create and return an EntityManagerFactory for the named persistence unit.
     *
     * @param persistenceUnitName The name of the persistence unit
     *
     * @return The factory that creates EntityManagers configured according to the specified persistence unit
     */
    fun createEntityManagerFactory(persistenceUnitName: String): Single<EntityManagerFactory> {
        return createEntityManagerFactory(persistenceUnitName, null)
    }

    /**
     * Create and return an EntityManagerFactory for the named persistence unit using the given properties.
     *
     * @param persistenceUnitName The name of the persistence unit
     * @param properties Additional properties to use when creating the factory. The values of these properties override
     * any values that may have been configured elsewhere
     *
     * @return The factory that creates EntityManagers configured according to the specified persistence unit
     */
    fun createEntityManagerFactory(persistenceUnitName: String, properties: Map<*, *>?): Single<EntityManagerFactory> = blockingSingle {
        Persistence.createEntityManagerFactory(persistenceUnitName, properties)
    }

    /**
     * Create database schemas and/or tables and/or create DDL scripts as determined by the supplied properties
     *
     * Called when schema generation is to occur as a separate phase from creation of the entity manager factory.
     *
     * @param persistenceUnitName the name of the persistence unit
     * @param properties properties for schema generation; these may also contain provider-specific properties. The
     * values of these properties override any values that may have been configured elsewhere.
     *
     * @throws PersistenceException if insufficient or inconsistent configuration information is provided or if schema
     * generation otherwise fails.
     */
    fun generateSchema(persistenceUnitName: String, properties: Map<*, *>) = blockingCompletable {
        Persistence.generateSchema(persistenceUnitName, properties)
    }

    /**
     * @return Returns a `PersistenceUtil` instance.
     */
    fun getPersistenceUtil(): PersistenceUtil = Persistence.getPersistenceUtil()

}
