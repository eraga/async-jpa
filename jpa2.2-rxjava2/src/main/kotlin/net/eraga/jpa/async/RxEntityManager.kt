package net.eraga.jpa.async

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.persistence.*


/**
 * @author tntclaus@gmail.com
 */

/**
 * Make an instance managed and persistent inside a transaction.
 * @param entity  entity instance
 * @param scheduler the [Scheduler] to perform subscription actions on. Defaults to [Schedulers.io]
 *
 * @throws EntityExistsException if the entity already exists.
 * (If the entity already exists, the <code>EntityExistsException</code> may
 * be thrown when the persist operation is invoked, or the
 * <code>EntityExistsException</code> or another <code>PersistenceException</code> may be
 * thrown at flush or commit time.)
 * @throws IllegalArgumentException if the instance is not an
 *         entity
 * @throws TransactionRequiredException if invoked on a
 *         container-managed entity manager of type
 *         <code>PersistenceContextType.TRANSACTION</code> and there is
 *         no transaction
 * @throws IllegalStateException if transaction is already active
 */
fun EntityManager.rxPersist(
        entity: Any,
        scheduler: Scheduler? = null
): Completable = Completable.create {
    try {
        synchronized(this) {
            transaction.begin()
            persist(entity)
            transaction.commit()
        }
        it.onComplete()
    } catch (e: Exception) {
        if (transaction.isActive)
            transaction.rollback()
        it.onError(e)
    }
}.subscribeOn(scheduler ?: Schedulers.io())

/**
 * Merge the state of the given entity into the
 * current persistence context inside a transaction.
 * @param entity  entity instance
 * @param scheduler the [Scheduler] to perform subscription actions on. Defaults to [Schedulers.io]
 *
 * @return the managed instance that the state was merged to
 * @throws IllegalArgumentException if instance is not an
 * entity or is a removed entity
 * @throws TransactionRequiredException if invoked on a
 * container-managed entity manager of type
 * `PersistenceContextType.TRANSACTION` and there is
 * no transaction
 * @throws IllegalStateException if transaction is already active
 */
fun <T> EntityManager.rxMerge(
        entity: T,
        scheduler: Scheduler? = null
): Single<T> = Single.create<T> {
    try {
        synchronized(this) {
            transaction.begin()
            val result = this.merge(entity)
            transaction.commit()
            it.onSuccess(result)
        }
    } catch (e: Exception) {
        if (transaction.isActive)
            transaction.rollback()
        it.onError(e)
    }
}.subscribeOn(scheduler ?: Schedulers.io())

/**
 * Remove the entity instance inside a transaction.
 *
 * @param entity  entity instance
 * @param scheduler the [Scheduler] to perform subscription actions on. Defaults to [Schedulers.io]
 *
 * @throws IllegalArgumentException if the instance is not an
 * entity or is a detached entity
 * @throws TransactionRequiredException if invoked on a
 * container-managed entity manager of type
 * `PersistenceContextType.TRANSACTION` and there is
 * no transaction
 * @throws IllegalStateException if transaction is already active
 */
fun EntityManager.rxRemove(
        entity: Any,
        scheduler: Scheduler? = null
): Completable = Completable.create {
    try {
        synchronized(this) {
            transaction.begin()
            remove(entity)
            transaction.commit()
        }
        it.onComplete()
    } catch (e: Exception) {
        if (transaction.isActive)
            transaction.rollback()
        it.onError(e)
    }
}.subscribeOn(scheduler ?: Schedulers.io())

/**
 * Find by primary key.
 * Search for an entity of the specified class and primary key.
 * If the entity instance is contained in the persistence context,
 * it is returned from there.
 * @param entityClass  entity class
 * @param primaryKey  primary key
 * @param scheduler the [Scheduler] to perform subscription actions on. Defaults to [Schedulers.io]
 *
 * @return the found entity instance
 * @throws IllegalArgumentException if the first argument does
 * not denote an entity type or the second argument is
 * is not a valid type for that entitys primary key or
 * is null
 * @throws NoResultException if the entity does not exist
 */
@Throws(NoResultException::class)
fun <T> EntityManager.rxFind(
        entityClass: Class<T>,
        primaryKey: Any,
        scheduler: Scheduler? = null): Single<T> = blockingSingle(scheduler) {
    this.find(entityClass, primaryKey)
            ?: throw NoResultException("No entries found for $entityClass with primary key $primaryKey")
}

/**
 * Execute code block inside of transaction.
 * @param scheduler the [Scheduler] to perform subscription actions on. Defaults to [Schedulers.io]
 * @param f code block to execute, receives a em [EntityManager] instance as an argument
 *
 * @throws IllegalStateException if transaction is already active
 */
fun EntityManager.rxTransaction(
        scheduler: Scheduler? = null,
        f: (em: EntityManager) -> Unit
): Completable = Completable.create {
    try {
        synchronized(this) {
            transaction.begin()
            f(this)
            transaction.commit()
        }
        it.onComplete()
    } catch (e: Exception) {
        if (transaction.isActive)
            transaction.rollback()
        it.onError(e)
    }
}.subscribeOn(scheduler ?: Schedulers.io())


/**
 * Execute code block inside of transaction.
 * @param scheduler the [Scheduler] to perform subscription actions on. Defaults to [Schedulers.io]
 * @param f code block to execute, receives a em [EntityManager] instance as an argument and returns
 * resulting instance
 *
 * @throws IllegalStateException if transaction is already active
 */
fun <T>EntityManager.rxTransaction(
        scheduler: Scheduler? = null,
        f: (em: EntityManager) -> T
): Maybe<T> = Maybe.create<T> {
    try {
        var result: T? = null

        synchronized(this) {
            transaction.begin()
            result = f(this)
            transaction.commit()
        }

        if(result == null) {
            it.onComplete()
        } else {
            it.onSuccess(result as T)
        }

    } catch (e: Exception) {
        if (transaction.isActive)
            transaction.rollback()
        it.onError(e)
    }
}.subscribeOn(scheduler ?: Schedulers.io())

/**
 * Synchronize the persistence context to the
 * underlying database.
 * @param scheduler the [Scheduler] to perform subscription actions on. Defaults to [Schedulers.io]
 *
 * @throws TransactionRequiredException if there is
 *         no transaction or if the entity manager has not been
 *         joined to the current transaction
 * @throws PersistenceException if the flush fails
 */
fun EntityManager.rxFlush(
        scheduler: Scheduler? = null
): Completable = Completable.create {
    try {
        synchronized(this) {
            transaction.begin()
            flush()
            transaction.commit()
        }
        it.onComplete()
    } catch (e: Exception) {
        if (transaction.isActive)
            transaction.rollback()
        it.onError(e)
    }
}.subscribeOn(scheduler ?: Schedulers.io())


/**
 * Refresh the state of the instance from the database,
 * overwriting changes made to the entity, if any.
 * @param entity  entity instance
 * @throws IllegalArgumentException if the instance is not
 * an entity or the entity is not managed
 * @throws TransactionRequiredException if there is no
 * transaction when invoked on a container-managed
 * entity manager of type `PersistenceContextType.TRANSACTION`
 * @throws EntityNotFoundException if the entity no longer
 * exists in the database
 */
fun EntityManager.rxRefresh(
        entity: Any,
        scheduler: Scheduler? = null
): Completable = Completable.create {
    try {
        synchronized(this) {
            transaction.begin()
            refresh(entity)
            transaction.commit()
        }
        it.onComplete()
    } catch (e: Exception) {
        if (transaction.isActive)
            transaction.rollback()
        it.onError(e)
    }
}.subscribeOn(scheduler ?: Schedulers.io())

/**
 * Refresh the state of the instance from the database, using
 * the specified properties, and overwriting changes made to
 * the entity, if any.
 *
 *  If a vendor-specific property or hint is not recognized,
 * it is silently ignored.
 * @param entity  entity instance
 * @param properties  standard and vendor-specific properties
 * and hints
 * @throws IllegalArgumentException if the instance is not
 * an entity or the entity is not managed
 * @throws TransactionRequiredException if there is no
 * transaction when invoked on a container-managed
 * entity manager of type `PersistenceContextType.TRANSACTION`
 * @throws EntityNotFoundException if the entity no longer
 * exists in the database
 * @since Java Persistence 2.0
 */
fun EntityManager.rxRefresh(
        entity: Any,
        properties: Map<String, Any>,
        scheduler: Scheduler? = null
): Completable = Completable.create {
    try {
        synchronized(this) {
            transaction.begin()
            refresh(entity, properties)
            transaction.commit()
        }
        it.onComplete()
    } catch (e: Exception) {
        if (transaction.isActive)
            transaction.rollback()
        it.onError(e)
    }
}.subscribeOn(scheduler ?: Schedulers.io())

/**
 * Refresh the state of the instance from the database,
 * overwriting changes made to the entity, if any, and
 * lock it with respect to given lock mode type.
 *
 * If the lock mode type is pessimistic and the entity instance
 * is found but cannot be locked:
 *
 *  *  the `PessimisticLockException` will be thrown if the database
 * locking failure causes transaction-level rollback
 *  *  the `LockTimeoutException` will be thrown if the
 * database locking failure causes only statement-level
 * rollback.
 *
 * @param entity  entity instance
 * @param lockMode  lock mode
 * @throws IllegalArgumentException if the instance is not
 * an entity or the entity is not managed
 * @throws TransactionRequiredException if invoked on a
 * container-managed entity manager of type
 * `PersistenceContextType.TRANSACTION` when there is
 * no transaction; if invoked on an extended entity manager when
 * there is no transaction and a lock mode other than `NONE`
 * has been specified; or if invoked on an extended entity manager
 * that has not been joined to the current transaction and a
 * lock mode other than `NONE` has been specified
 * @throws EntityNotFoundException if the entity no longer exists
 * in the database
 * @throws PessimisticLockException if pessimistic locking fails
 * and the transaction is rolled back
 * @throws LockTimeoutException if pessimistic locking fails and
 * only the statement is rolled back
 * @throws PersistenceException if an unsupported lock call
 * is made
 * @since Java Persistence 2.0
 */
fun EntityManager.rxRefresh(
        entity: Any,
        lockMode: LockModeType,
        scheduler: Scheduler? = null
): Completable = Completable.create {
    try {
        synchronized(this) {
            transaction.begin()
            refresh(entity, lockMode)
            transaction.commit()
        }
        it.onComplete()
    } catch (e: Exception) {
        if(transaction.isActive)
            transaction.rollback()
        it.onError(e)
    }
}.subscribeOn(scheduler ?: Schedulers.io())

/**
 * Refresh the state of the instance from the database,
 * overwriting changes made to the entity, if any, and
 * lock it with respect to given lock mode type and with
 * specified properties.
 *
 * If the lock mode type is pessimistic and the entity instance
 * is found but cannot be locked:
 *
 *  *  the `PessimisticLockException` will be thrown if the database
 * locking failure causes transaction-level rollback
 *  *  the `LockTimeoutException` will be thrown if the database
 * locking failure causes only statement-level rollback
 *
 *
 * If a vendor-specific property or hint is not recognized,
 * it is silently ignored.
 *
 * Portable applications should not rely on the standard timeout
 * hint. Depending on the database in use and the locking
 * mechanisms used by the provider, the hint may or may not
 * be observed.
 * @param entity  entity instance
 * @param lockMode  lock mode
 * @param properties  standard and vendor-specific properties
 * and hints
 * @throws IllegalArgumentException if the instance is not
 * an entity or the entity is not managed
 * @throws TransactionRequiredException if invoked on a
 * container-managed entity manager of type
 * `PersistenceContextType.TRANSACTION` when there is
 * no transaction; if invoked on an extended entity manager when
 * there is no transaction and a lock mode other than `NONE`
 * has been specified; or if invoked on an extended entity manager
 * that has not been joined to the current transaction and a
 * lock mode other than `NONE` has been specified
 * @throws EntityNotFoundException if the entity no longer exists
 * in the database
 * @throws PessimisticLockException if pessimistic locking fails
 * and the transaction is rolled back
 * @throws LockTimeoutException if pessimistic locking fails and
 * only the statement is rolled back
 * @throws PersistenceException if an unsupported lock call
 * is made
 * @since Java Persistence 2.0
 */
fun EntityManager.rxRefresh(
        entity: Any,
        lockMode: LockModeType,
        properties: Map<String, Any>,
        scheduler: Scheduler? = null
): Completable = Completable.create {
    try {
        synchronized(this) {
            transaction.begin()
            refresh(entity, lockMode, properties)
            transaction.commit()
        }
        it.onComplete()
    } catch (e: Exception) {
        if(transaction.isActive)
            transaction.rollback()
        it.onError(e)
    }
}.subscribeOn(scheduler ?: Schedulers.io())
