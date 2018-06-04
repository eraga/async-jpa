package net.eraga.jpa.async

import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import javafx.beans.value.ObservableValue
import java.util.stream.Stream
import io.reactivex.Flowable
import java.util.stream.Collectors


/**
 * Date: 15/01/2018
 * Time: 04:13
 * @author tntclaus@gmail.com
 */


/**
 * Wrap a Stream into a Observable.
 *
 * Note that Streams can only be consumed once and non-concurrently.
 *
 * from: https://github.com/akarnokd/RxJava2Jdk8Interop
 *
 * @param <T> the value type
 * @param stream the source Stream
 * @return the new Observable instance
</T> */
internal fun <T> observableFromStream(stream: Stream<T>): Observable<T> {
    return Observable.fromIterable(object : MutableIterable<T> {
        override fun iterator(): MutableIterator<T> {
            return stream.iterator()
        }
    })
}


/**
 * Wrap a Stream into a Flowable.
 *
 * Note that Streams can only be consumed once and non-concurrently.
 *
 * from: https://github.com/akarnokd/RxJava2Jdk8Interop
 *
 * @param <T> the value type
 * @param stream the source Stream
 * @return the new Flowable instance
</T> */
internal fun <T> flowableFromStream(stream: Stream<T>): Flowable<T> {
    return Flowable.fromIterable(object : MutableIterable<T> {
        override fun iterator(): MutableIterator<T> {
            return stream.iterator()
        }
    })
}


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

inline fun <T> observableWithStream(emitter: ObservableEmitter<T>, f: () -> Stream<T>) {
    try {
        val stream = f()
//        stream.collect({
//
//        },{ t, u ->
//
//        }, { t, u ->
//
//        })
        stream.iterator().forEachRemaining {
            emitter.onNext(it)
        }
//        stream.forEach { element ->
//            emitter.onNext(element)
//        }
    } catch (e: Exception) {
        emitter.onError(e)
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
): Single<T> = Single.create<T> {
    single(it) { f() }
}.subscribeOn(scheduler ?: Schedulers.io())


fun <T> streamObservable(
        stream: Stream<T>,
        scheduler: Scheduler? = null
): Observable<T> = observableFromStream(stream).subscribeOn(scheduler ?: Schedulers.io())


//        Observable.create<T> {
//
//}.subscribeOn(scheduler ?: Schedulers.io())


