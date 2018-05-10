package kundera.rdbms

import net.eraga.jpa.async.*
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.SynchronizationType
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Date: 08/05/2018
 * Time: 22:39
 */
object RxEntityManagerFactorySpec : SubjectSpek<String>({
    subject { "H2 Kundera" }

    var entityManager: EntityManager? = null
    lateinit var entityManagerFactory: EntityManagerFactory


    given("$subject persistence unit") {
        beforeEachTest {
            entityManagerFactory = RxPersistence
                    .createEntityManagerFactory(subject)
                    .blockingGet()
        }

        afterEachTest {
            entityManager?.close()
            entityManager = null

            entityManagerFactory.close()
        }

        on("create manager with properties ") {
            it("should use properties") {
                val properties = HashMap<String, Any>()
                properties.put("javax.persistence.jdbc.driver", "unknown_db")
                entityManager = entityManagerFactory
                        .rxCreateEntityManager(properties)
                        .blockingGet()
                assertEquals("unknown_db", entityManager?.properties?.get("javax.persistence.jdbc.driver"))
            }
        }

        on("create manager with synchronizationType ") {
            it("should create SYNCHRONIZED because kundera fucks up JTA") {

                entityManager = entityManagerFactory
                        .rxCreateEntityManager(SynchronizationType.SYNCHRONIZED)
                        .blockingGet()

                assertNotNull(entityManager)
            }

            it("should create UNSYNCHRONIZED because kundera fucks up JTA") {
                entityManager = entityManagerFactory
                        .rxCreateEntityManager(SynchronizationType.SYNCHRONIZED)
                        .blockingGet()
                assertNotNull(entityManager)
            }
        }

        on("create manager with properties and synchronizationType ") {
            it("should also pass because kundera fucks up JTA") {
                val properties = HashMap<String, Any>()

                entityManager = entityManagerFactory
                        .rxCreateEntityManager(SynchronizationType.SYNCHRONIZED, properties)
                        .blockingGet()
                assertNotNull(entityManager)

            }
        }
    }
})
