package hibernate

import Book
import net.eraga.jpa.async.*
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import kotlin.test.*

/**
 * Date: 08/05/2018
 * Time: 21:23
 */
object RxEntityManagerSpec : SubjectSpek<String>({
    subject { "H2 Hibernate" }

    lateinit var entityManager: EntityManager
    lateinit var entityManagerFactory: EntityManagerFactory

    given("$subject persistence unit") {
        beforeEachTest {
            entityManagerFactory = RxPersistence
                    .createEntityManagerFactory(subject)
                    .blockingGet()

            entityManager = entityManagerFactory
                    .rxCreateEntityManager()
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

                error?.printStackTrace()
                assertNull(error, error?.message)
            }

            it("should update changed field in db") {
                book.text = "persisted text"

                val bookNew = entityManager
                        .rxFind(Book::class.java, 1)
                        .blockingGet()

                assertNotNull(bookNew)

                assertEquals("persisted text", bookNew.text)
            }
        }

        on("merging entity to DB") {
            val book = Book("rxMergeBook")
            lateinit var bookMerged: Book

            it("should merge Book in DB") {
                bookMerged = entityManager
                        .rxMerge(book)
                        .blockingGet()


                val bookModified = entityManager
                        .rxFind(Book::class.java, 1)
                        .blockingGet()

                assertEquals("rxMergeBook", bookModified.title)
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
//      todo investigate this
//        on("removing non existent entity from DB") {
//            it("should fail to remove") {
//                assertFails {
//                    entityManager
//                            .rxFind(Book::class.java, 12000)
//                            .blockingGet()
//                }
//
//                assertFailsWith<IllegalArgumentException> {
//                    val error = entityManager
//                            .rxRemove(Book().apply { id = 12000 })
//                            .blockingGet()
//
//                    if (error != null)
//                        throw error
//                }
//            }
//        }

        on("removing entity from DB") {
            it("should fail to remove detached entity") {
                val book = Book("To be not deleted")
                entityManager
                        .rxPersist(book)
                        .blockingGet()

                assertFailsWith<IllegalArgumentException> {
                    val error = entityManager
                            .rxRemove(Book().apply { id = 1 })
                            .blockingGet()

                    if (error != null)
                        throw error
                }
            }

            it("should remove entity") {
                var book = Book("To be deleted")
                book = entityManager
                        .rxMerge(book)
                        .blockingGet()

                val error = entityManager
                        .rxRemove(book)
                        .blockingGet()

                assertNull(error)

                if (error != null)
                    throw error



                assertFails {
                    entityManager
                            .rxFind(Book::class.java, 2)
                            .blockingGet()
                }
            }
        }



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
