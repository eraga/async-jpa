package kundera.rdbms

import net.eraga.rxjpa2.RxPersistence
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceException
import javax.persistence.PersistenceUtil
import kotlin.test.*

/**
 * Date: 08/05/2018
 * Time: 18:16
 */
object RxPersistenceSpec : SubjectSpek<String>({
    subject { "H2 Kundera" }

    given("$subject persistence unit") {
        on("createEntityManagerFactory ") {
            it("should create with  with persistenceUnitName") {
                val emf = RxPersistence
                        .createEntityManagerFactory(subject)
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
                                subject,
                                properties
                        )
                        .blockingGet()

                val success = emf is EntityManagerFactory

                if (success) emf.close()

                assertTrue { success }
            }

            it("should fail with with wrong url in properties") {

                assertFails {
                    val properties = Properties()
                    properties.setProperty("hibernate.connection.url", "test_url")

                    RxPersistence
                            .createEntityManagerFactory(
                                    subject,
                                    properties
                            )
                            .blockingGet()

                }
            }
        }

        on("generate schema") {
            it("should fail to generate schema") {

                val properties = Properties()

                val error = RxPersistence
                        .generateSchema(subject, properties)
                        .blockingGet()

                assertNotNull(error)
                assertEquals(error::class.java, PersistenceException::class.java)
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
