package net.eraga.jpa.async

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

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
