import net.eraga.rxjpa2.RxPersistence
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceException
import javax.persistence.PersistenceUtil
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Date: 08/05/2018
 * Time: 18:16
 */
class RxPersistenceSpec : Spek({
    given("RxPersistence with H2 memory db") {
        on("createEntityManagerFactory ") {
            it("should create with  with persistenceUnitName") {
                val emf = RxPersistence
                        .createEntityManagerFactory("rxJpa2-test")
                        .blockingGet()

                val success = emf is EntityManagerFactory

                if (success) emf.close()

                assertTrue { success }
            }

            it("should create with  with persistenceUnitName and properties") {

                val properties = Properties()
                properties.setProperty("javax.persistence.jdbc.url", "jdbc:h2:mem:test")

                val emf = RxPersistence
                        .createEntityManagerFactory(
                                "rxJpa2-test",
                                properties
                        )
                        .blockingGet()

                val success = emf is EntityManagerFactory

                if (success) emf.close()

                assertTrue { success }
            }

            it("should fail with with wrong url in properties") {

                try {
                    val properties = Properties()
                    properties.setProperty("javax.persistence.jdbc.url", "test_url")

                    RxPersistence
                            .createEntityManagerFactory(
                                    "rxJpa2-test-eclipse",
                                    properties
                            )
                            .blockingGet()

                } catch (e: PersistenceException) {
                    assertTrue {
//                        e.cause?.cause?.message == "Unable to make JDBC Connection [test_url]" —— Hibernate error
                        e.cause?.cause?.message == "No suitable driver found for test_url"
                    }
                }
            }
        }

        on("generate schema") {
            it("should generate schema") {
                val properties = Properties()
                properties.setProperty("javax.persistence.jdbc.url", "jdbc:h2:mem:test")

                val error = RxPersistence
                        .generateSchema("rxJpa2-test-eclipse", properties)
                        .blockingGet()


                assertNull(error)

                if(error != null) {
                    throw error
                }
            }
        }


        on("getPersistenceUtil") {
            it("should return PersistenceUtil") {
                assertTrue {
                    // This can't break, but we live in the world full of magic ^_^
                    // Вжух и пиздец...
                    @Suppress("USELESS_IS_CHECK")
                    RxPersistence.getPersistenceUtil() is PersistenceUtil
                }
            }
        }



    }



    afterGroup {
//        System.exit(0)
    }


})
