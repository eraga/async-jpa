package net.eraga.rxjpa2

import io.reactivex.Scheduler
import io.reactivex.Single
import javax.persistence.*

/**
 * Date: 15/01/2018
 * Time: 06:33
 * @author tntclaus@gmail.com
 */

/**
 * Execute a SELECT query and return the query results
 * as a typed List.
 *
 * @return a list of the results
 *
 * @throws IllegalStateException if called for a Java
 * Persistence query language UPDATE or DELETE statement
 * @throws QueryTimeoutException if the query execution exceeds
 * the query timeout value set and only the statement is
 * rolled back
 * @throws TransactionRequiredException if a lock mode has
 * been set and there is no transaction
 * @throws PessimisticLockException if pessimistic locking
 * fails and the transaction is rolled back
 * @throws LockTimeoutException if pessimistic locking
 * fails and only the statement is rolled back
 * @throws PersistenceException if the query execution exceeds
 * the query timeout value set and the transaction
 * is rolled back
 */
fun <T> TypedQuery<T>.rxResultList(
        scheduler: Scheduler? = null
): Single<List<T>> = blockingSingle(scheduler) {
    this.resultList
}


/**
 * Execute a SELECT query that returns a single result.
 *
 * @return the result
 *
 * @throws NoResultException if there is no result
 * @throws NonUniqueResultException if more than one result
 * @throws IllegalStateException if called for a Java
 * Persistence query language UPDATE or DELETE statement
 * @throws QueryTimeoutException if the query execution exceeds
 * the query timeout value set and only the statement is
 * rolled back
 * @throws TransactionRequiredException if a lock mode has
 * been set and there is no transaction
 * @throws PessimisticLockException if pessimistic locking
 * fails and the transaction is rolled back
 * @throws LockTimeoutException if pessimistic locking
 * fails and only the statement is rolled back
 * @throws PersistenceException if the query execution exceeds
 * the query timeout value set and the transaction
 * is rolled back
 */
fun <T> TypedQuery<T>.rxSingleResult(
        scheduler: Scheduler? = null
): Single<T> = blockingSingle(scheduler) {
    this.singleResult
}

/**
 * Execute a SELECT query and return the query results
 * as an untyped List.
 *
 * @return a list of the results
 *
 * @throws IllegalStateException if called for a Java
 * Persistence query language UPDATE or DELETE statement
 * @throws QueryTimeoutException if the query execution exceeds
 * the query timeout value set and only the statement is
 * rolled back
 * @throws TransactionRequiredException if a lock mode has
 * been set and there is no transaction
 * @throws PessimisticLockException if pessimistic locking
 * fails and the transaction is rolled back
 * @throws LockTimeoutException if pessimistic locking
 * fails and only the statement is rolled back
 * @throws PersistenceException if the query execution exceeds
 * the query timeout value set and the transaction
 * is rolled back
 */
fun Query.rxResultList(
        scheduler: Scheduler? = null
): Single<List<Any?>> = blockingSingle(scheduler) {
    this.resultList
}

/**
 * Execute a SELECT query that returns a single untyped result.
 *
 * @return the result
 *
 * @throws NoResultException if there is no result
 * @throws NonUniqueResultException if more than one result
 * @throws IllegalStateException if called for a Java
 * Persistence query language UPDATE or DELETE statement
 * @throws QueryTimeoutException if the query execution exceeds
 * the query timeout value set and only the statement is
 * rolled back
 * @throws TransactionRequiredException if a lock mode has
 * been set and there is no transaction
 * @throws PessimisticLockException if pessimistic locking
 * fails and the transaction is rolled back
 * @throws LockTimeoutException if pessimistic locking
 * fails and only the statement is rolled back
 * @throws PersistenceException if the query execution exceeds
 * the query timeout value set and the transaction
 * is rolled back
 */
fun Query.rxSingleResult(
        scheduler: Scheduler? = null
): Single<Any> = blockingSingle(scheduler) {
    this.singleResult
}

/**
 * The position of the first result the query object was set to
 * retrieve. Returns 0 if <code>setFirstResult</code> was not applied to the
 * query object.
 *
 * @return position of the first result
 *
 * @since Java Persistence 2.0
 */
fun Query.rxFirstResult(
        scheduler: Scheduler? = null
): Single<Int> = blockingSingle(scheduler) {
    this.firstResult
}

/**
 * Execute an update or delete statement.
 *
 * @param em optional [EntityManager] instance if you want to
 * begin/commit transaction automatically
 *
 * @return the number of entities updated or deleted
 *
 * @throws IllegalStateException if called for a Java
 * Persistence query language SELECT statement or for
 * a criteria query
 * @throws TransactionRequiredException if there is
 * no transaction
 * @throws QueryTimeoutException if the statement execution
 * exceeds the query timeout value set and only
 * the statement is rolled back
 * @throws PersistenceException if the query execution exceeds
 * the query timeout value set and the transaction
 * is rolled back
 */
fun Query.rxExecuteUpdate(
        em: EntityManager? = null,
        scheduler: Scheduler? = null
): Single<Int> = blockingSingle(scheduler) {
    em?.transaction?.begin()
    val result = this.executeUpdate()
    em?.transaction?.commit()
    result
}

/**
 * Execute an update or delete statement.
 *
 * @param transaction [EntityTransaction] instance if you want to
 * begin/join existing transaction
 * @param commit if you want to commit transaction, defaults to false
 *
 * @return the number of entities updated or deleted
 *
 * @throws IllegalStateException if called for a Java
 * Persistence query language SELECT statement or for
 * a criteria query
 * @throws TransactionRequiredException if there is
 * no transaction
 * @throws QueryTimeoutException if the statement execution
 * exceeds the query timeout value set and only
 * the statement is rolled back
 * @throws PersistenceException if the query execution exceeds
 * the query timeout value set and the transaction
 * is rolled back
 */
fun Query.rxExecuteUpdate(
        transaction: EntityTransaction,
        commit: Boolean = false,
        scheduler: Scheduler? = null
): Single<Int> = blockingSingle(scheduler) {
    if(!transaction.isActive)
        transaction.begin()
    val result = this.executeUpdate()
    if(commit)
        transaction.commit()
    result
}
