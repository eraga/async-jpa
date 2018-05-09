package kundera.rdbms

import Book
import net.eraga.rxjpa2.*
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.slf4j.LoggerFactory
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import kotlin.test.*

/**
 * Date: 08/05/2018
 * Time: 21:23
 */
object RxEntityManagerSpec : SubjectSpek<String>({
    System.setProperty("org.jboss.logging.provider", "slf4j")

    subject { "H2 Kundera" }

    lateinit var entityManager: EntityManager
    lateinit var entityManagerFactory: EntityManagerFactory

    given("$subject persistence unit") {
        beforeEachTest {

            RxPersistence
                    .createEntityManagerFactory(subject)
                    .flatMap {
                        entityManagerFactory = it
                        it.rxCreateEntityManager()
                    }.map {
                        entityManager = it
                    }
                    .blockingGet()
        }

        afterEachTest {
            entityManager.close()
            entityManagerFactory.close()
        }

        on("persisting entity in db") {
            val book = Book("rxPersistBook")
            it("should persist Book in DB") {
                val error = entityManager
                        .rxPersist(book)
                        .blockingGet()

                if (error != null)
                    throw error

                assertNull(error)
            }
//            WARNING: Kundera fails this test TODO file a bug
//            it("should update changed field in db") {
//                book.text = "persisted text"
//                entityManager.transaction.begin()
//                entityManager.flush()
//                entityManager.transaction.commit()
//
//
//                val bookNew = entityManager
//                        .rxFind(Book::class.java, 1)
//                        .blockingGet()
//
//                assertNotNull(bookNew)
//
//                assertEquals("persisted text", bookNew.text)
//            }
        }

        on("merging entity to DB") {
            val book = Book("rxMergeBook")
            lateinit var bookMerged: Book

            it("should merge Book in DB") {
                entityManager
                        .rxPersist(book)
                        .blockingGet()

                bookMerged = entityManager
                        .rxMerge(Book("Modified book").apply { id = 1 })
                        .blockingGet()

                val bookModified = entityManager
                        .rxFind(Book::class.java, 1)
                        .blockingGet()

                assertEquals("Modified book", bookModified.title)
            }

            it("should not update field of original entity in db") {
                book.text = "persisted text"


                val bookNew = entityManager
                        .rxFind(Book::class.java, 1)
                        .blockingGet()

                assertNotNull(bookNew)

                assertNotEquals("persisted text", bookNew.text)
            }

            it("should update field of merged entity in db") {
                bookMerged.text = "persisted text"

                val bookNew = entityManager
                        .rxFind(Book::class.java, 1)
                        .blockingGet()


                assertEquals("persisted text", bookNew.text)
            }
        }
//            WARNING: Kundera fails this test TODO file a bug
//        on("removing detached entity from DB") {
//            it("should fail to remove detached entity") {
//                val book = Book("To be not deleted")
//                entityManager
//                        .rxPersist(book)
//                        .blockingGet()
//
////                log.info("We have it? {}", entityManager.contains(Book("To be not deleted").apply { id = 1 }))
//
//                assertFailsWith<IllegalArgumentException> {
//                    val error = entityManager
//                            .rxRemove(Book().apply { id = 1 })
//                            .blockingGet()
//
//                    if (error != null)
//                        throw error
//                }
//            }
//        }

//            WARNING: Kundera fails this test TODO file a bug
//        on("removing attached entity from DB") {
//            it("should remove entity") {
//                val book = Book("To be deleted")
//                entityManager
//                        .rxPersist(book)
//                        .blockingGet()
//
//                val error = entityManager
//                        .rxRemove(book)
//                        .blockingGet()
//
//                error?.printStackTrace()
//                assertNull(error, error?.message)
//
//                assertFails {
//                    entityManager
//                            .rxFind(Book::class.java, 1)
//                            .blockingGet()
//                }
//            }
//        }



        on("rxTransaction") {
            it("should store 4 books in transaction") {
                entityManager
                        .rxTransaction {
                            it.persist(Book("Transaction book 1"))
                            it.persist(Book("Transaction book 2"))
                            it.persist(Book("Transaction book 3"))
                            it.persist(Book("Transaction book 4"))
                        }
                        .blockingGet()

                assertNotNull(entityManager.rxFind(Book::class.java, 4))
            }
        }
    }
})
