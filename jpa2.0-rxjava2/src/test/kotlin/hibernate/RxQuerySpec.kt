package hibernate

import TestSubject
import Book
import net.eraga.jpa.async.*
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import kotlin.test.assertEquals

/**
 * Date: 08/05/2018
 * Time: 23:22
 */
object RxQuerySpec : SubjectSpek<TestSubject>({
    subject { TestSubject("H2 Hibernate") }

    lateinit var entityManager: EntityManager
    lateinit var entityManagerFactory: EntityManagerFactory

    given("$subject persistence unit") {
        beforeEachTest {
            val properties = Properties()
            entityManagerFactory = RxPersistence
                    .createEntityManagerFactory(subject.persistenceUnit, properties)
                    .blockingGet()

            entityManager = entityManagerFactory
                    .rxCreateEntityManager()
                    .blockingGet()

            entityManager
                    .rxTransaction {
                        it.persist(Book("First book"))
                        it.persist(Book("Second book"))
                        it.persist(Book("Third book"))
                        it.persist(Book("Fourth book"))
                    }
                    .blockingGet()

        }

        afterEachTest {
            entityManager.close()
            entityManagerFactory.close()
        }

        on("executing typed queries") {
            it("should have rxResultList with 4 rows") {

                val result = entityManager
                        .createQuery(
                                "SELECT b From Book b where b.title like :title",
                                Book::class.java
                        )
                        .setParameter("title", "%book")
                        .rxResultList()
                        .blockingGet()

                assertEquals(4, result.size)
            }

            it("should have rxSingleResult with id 1") {

                val result = entityManager
                        .createQuery(
                                "SELECT b From Book b where b.title = :title",
                                Book::class.java
                        )
                        .setParameter("title", "First book")
                        .rxSingleResult()
                        .blockingGet()

                assertEquals(1, result.id)
            }
        }

        on("executing queries") {
            it("should have rxResultList with 4 rows") {

                val result = entityManager
                        .createQuery(
                                "SELECT b From Book b where b.title like :title"
                        )
                        .setParameter("title", "%book")
                        .rxResultList()
                        .blockingGet()

                assertEquals(4, result.size)
            }

            it("should have rxFirstResult with position 1") {

                val result = entityManager
                        .createQuery(
                                "SELECT b From Book b where b.title = :title"
                        )
                        .setParameter("title", "Third book")
                        .setFirstResult(1)
                        .rxFirstResult()
                        .blockingGet()

                assertEquals(1, result)
            }

            it("should have rxSingleResult with id 1") {

                val result = entityManager
                        .createQuery(
                                "SELECT b From Book b where b.title = :title"
                        )
                        .setParameter("title", "First book")
                        .rxSingleResult()
                        .blockingGet() as Book

                assertEquals(1, result.id)
            }

            it("should do rxExecuteUpdate with 1 row deleted (auto transaction)") {
                entityManager.transaction
                val result = entityManager
                        .createQuery(
                                "DELETE From Book b where b.title = :title"
                        )
                        .setParameter("title", "First book")
                        .rxExecuteUpdate(entityManager)
                        .blockingGet()

                assertEquals(1, result)
            }

            it("should do rxExecuteUpdate with 1 row deleted (manual transaction)") {
                entityManager.transaction.begin()
                val result = entityManager
                        .createQuery(
                                "DELETE From Book b where b.title = :title"
                        )
                        .setParameter("title", "Second book")
                        .rxExecuteUpdate()
                        .blockingGet()
                entityManager.transaction.commit()

                assertEquals(1, result)
            }



            it("should do rxExecuteUpdate with transaction parameter") {
                entityManager.transaction.begin()

                val result1 = entityManager
                        .createQuery(
                                "DELETE From Book b where b.title = :title"
                        )
                        .setParameter("title", "Third book")
                        .rxExecuteUpdate(entityManager.transaction)
                        .blockingGet()

                val result2 = entityManager
                        .createQuery(
                                "DELETE From Book b where b.title = :title"
                        )
                        .setParameter("title", "Fourth book")
                        .rxExecuteUpdate(entityManager.transaction, true)
                        .blockingGet()

                assertEquals(1, result1)
                assertEquals(1, result2)
            }
        }
    }
})
