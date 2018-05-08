package net.eraga.rxjpa2

import io.reactivex.*
import io.reactivex.schedulers.Schedulers

/**
 * Date: 15/01/2018
 * Time: 04:13
 * @author tntclaus@gmail.com
 */


inline fun <T> single(it: SingleEmitter<T>, f: () -> T) {
    try {
        it.onSuccess(f())
    } catch (e: Exception) {
        it.onError(e)
    }
}

inline fun completable(it: CompletableEmitter, f: () -> Unit) {
    try {
        f()
        it.onComplete()
    } catch (e: Exception) {
        it.onError(e)
    }
}

inline fun blockingCompletable(
        scheduler: Scheduler? = null,
        crossinline f: () -> Unit
): Completable = Completable.create {
    completable(it) { f() }
}.subscribeOn(scheduler ?: Schedulers.io())

inline fun <T> blockingSingle(
        scheduler: Scheduler? = null,
        crossinline f: () -> T
): Single<T> =  Single.create<T> {
    single(it) { f() }
}.subscribeOn(scheduler ?: Schedulers.io())


