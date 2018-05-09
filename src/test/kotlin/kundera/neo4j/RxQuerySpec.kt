package kundera.neo4j

import Neo4jBook
import net.eraga.rxjpa2.*
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.api.dsl.xgiven
import org.jetbrains.spek.subject.SubjectSpek
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Date: 08/05/2018
 * Time: 23:22
 */
object RxQuerySpec : SubjectSpek<String>({
    subject { "neo4j Kundera" }

    lateinit var entityManager: EntityManager
    lateinit var entityManagerFactory: EntityManagerFactory

    xgiven("$subject persistence unit") {
        beforeEachTest {
            val properties = Properties()
            entityManagerFactory = RxPersistence
                    .createEntityManagerFactory(subject, properties)
                    .blockingGet()

            entityManager = entityManagerFactory
                    .rxCreateEntityManager()
                    .blockingGet()

            entityManager
                    .rxTransaction {
                        it.persist(Neo4jBook("First book"))
                        it.persist(Neo4jBook("Second book"))
                        it.persist(Neo4jBook("Third book"))
                        it.persist(Neo4jBook("Fourth book"))
                    }
                    .blockingGet()?.printStackTrace()

        }

        afterEachTest {
            entityManager.close()
            entityManagerFactory.close()
        }

        on("executing typed queries") {
            it("should have rxResultList with 4 rows") {

                val result = entityManager
                        .createQuery(
                                "SELECT b From Neo4jBook b where b.title like :title",
                                Neo4jBook::class.java
                        )
                        .setParameter("title", "%book")
                        .rxResultList()
                        .blockingGet()

                assertEquals(4, result.size)
            }

            it("should have rxSingleResult with id 1") {

                val result = entityManager
                        .createQuery(
                                "SELECT b From Neo4jBook b where b.title = :title",
                                Neo4jBook::class.java
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
                                "SELECT b From Neo4jBook b where b.title like :title"
                        )
                        .setParameter("title", "%book")
                        .rxResultList()
                        .blockingGet()

                assertEquals(4, result.size)
            }

            it("should fail rxFirstResult as it is unsupported by Kundera") {
                assertFailsWith<UnsupportedOperationException> {
//                    java.lang.UnsupportedOperationException: getFirstResult is unsupported by Kundera
                    val result = entityManager
                            .createQuery(
                                    "SELECT b From Neo4jBook b where b.title = :title"
                            )
                            .setParameter("title", "Third book")
                            .setFirstResult(1)
                            .rxFirstResult()
                            .blockingGet()
                }
            }

            it("should have rxSingleResult with id 1") {

                val result = entityManager
                        .createQuery(
                                "SELECT b From Neo4jBook b where b.title = :title"
                        )
                        .setParameter("title", "First book")
                        .rxSingleResult()
                        .blockingGet() as Neo4jBook

                assertEquals(1, result.id)
            }

            it("should do rxExecuteUpdate with 1 row deleted (auto transaction)") {
                entityManager.transaction
                val result = entityManager
                        .createQuery(
                                "DELETE From Neo4jBook b where b.title = :title"
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
                                "DELETE From Neo4jBook b where b.title = :title"
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
                                "DELETE From Neo4jBook b where b.title = :title"
                        )
                        .setParameter("title", "Third book")
                        .rxExecuteUpdate(entityManager.transaction)
                        .blockingGet()

                val result2 = entityManager
                        .createQuery(
                                "DELETE From Neo4jBook b where b.title = :title"
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
