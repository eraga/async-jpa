package net.eraga.jpa.async

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.SynchronizationType

/**
 * @author tntclaus@gmail.com
 */

/**
 * Rx wrapper for [EntityManagerFactory.createEntityManager]
 * @param scheduler the [Scheduler] to perform subscription actions on. Defaults to [Schedulers.io]
 *
 */
fun EntityManagerFactory.rxCreateEntityManager(
        scheduler: Scheduler? = null
): Single<EntityManager> = blockingSingle(scheduler) {
    this.createEntityManager()
}

/**
 * Rx wrapper for [EntityManagerFactory.createEntityManager]
 *
 * @param scheduler the [Scheduler] to perform subscription actions on. Defaults to [Schedulers.io]
 *
 */
fun EntityManagerFactory.rxCreateEntityManager(
        map: Map<*, *>,
        scheduler: Scheduler? = null
): Single<EntityManager> = blockingSingle(scheduler) {
    this.createEntityManager(map)
}

/**
 * Rx wrapper for [EntityManagerFactory.createEntityManager]
 *
 * @param scheduler the [Scheduler] to perform subscription actions on. Defaults to [Schedulers.io]
 *
 */
fun EntityManagerFactory.rxCreateEntityManager(
        synchronizationType: SynchronizationType,
        map: MutableMap<*, *>,
        scheduler: Scheduler? = null
): Single<EntityManager> = blockingSingle(scheduler) {
    this.createEntityManager(synchronizationType, map)
}

/**
 * Rx wrapper for [EntityManagerFactory.createEntityManager]
 *
 * Create a new JTA application-managed EntityManager with the specified synchronization type.  This method
 * returns a new EntityManager instance each time it is invoked.  The isOpen method will return true on the
 * returned instance.
 *
 * @param synchronizationType how and when the entity manager should be synchronized with the current JTA
 * transaction
 * @param scheduler the [Scheduler] to perform subscription actions on. Defaults to [Schedulers.io]
 *
 * @return [Single] that emits [EntityManager] instance
 *
 * @throws IllegalStateException if the entity manager factory has been configured for resource-local entity
 * managers or has been closed
 */
fun EntityManagerFactory.rxCreateEntityManager(
        synchronizationType: SynchronizationType,
        scheduler: Scheduler? = null
): Single<EntityManager> = blockingSingle(scheduler) {
    this.createEntityManager(synchronizationType)
}
