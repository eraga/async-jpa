package hibernate

import net.eraga.rxjpa2.RxPersistence
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceException
import javax.persistence.PersistenceUtil
import kotlin.test.assertFails
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Date: 08/05/2018
 * Time: 18:16
 */
object RxPersistenceSpec : SubjectSpek<String>({
    subject { "H2 Hibernate" }

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
                    properties.setProperty("javax.persistence.jdbc.url", "test_url")

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
            it("should generate schema") {
                if(!subject.toLowerCase().contains("hibernate")) {
                    val properties = Properties()
                    properties.setProperty("javax.persistence.jdbc.url", "jdbc:h2:mem:test")

                    val error = RxPersistence
                            .generateSchema(subject, properties)
                            .blockingGet()


                    assertNull(error)

                    if (error != null) {
                        throw error
                    }
                } else {
                    println("Ignoring this test for $subject. Issue with Hibernate: " +
                            "it doesn't drop connection to DB after schema generation. " +
                            "Tests won't complete.")
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
