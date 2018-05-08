import net.eraga.rxjpa2.RxPersistence
import net.eraga.rxjpa2.rxCreateEntityManager
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.SynchronizationType
import kotlin.test.assertEquals
import kotlin.test.assertFails

/**
 * Date: 08/05/2018
 * Time: 22:39
 */
class RxEntityManagerFactorySpec : Spek({
    var entityManager: EntityManager? = null
    lateinit var entityManagerFactory: EntityManagerFactory


    given("EntityManagerFactory with H2 memory db") {
        beforeEachTest {
            entityManagerFactory = RxPersistence
                    .createEntityManagerFactory("rxJpa2-test")
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
            it("should fail to create SYNCHRONIZED because we have no JTA") {
                assertFails {
                    entityManager = entityManagerFactory
                            .rxCreateEntityManager(SynchronizationType.SYNCHRONIZED)
                            .blockingGet()
                }
            }

            it("should fail to create UNSYNCHRONIZED because we have no JTA") {
                assertFails {
                    entityManager = entityManagerFactory
                            .rxCreateEntityManager(SynchronizationType.SYNCHRONIZED)
                            .blockingGet()
                }
            }
        }

        on("create manager with properties and synchronizationType ") {
            it("should also fail because we have no JTA") {
                val properties = HashMap<String, Any>()
                properties.put("javax.persistence.jdbc.driver", "unknown_db")
                assertFails {
                    entityManager = entityManagerFactory
                            .rxCreateEntityManager(SynchronizationType.SYNCHRONIZED, properties)
                            .blockingGet()
                }

            }
        }
    }
})
