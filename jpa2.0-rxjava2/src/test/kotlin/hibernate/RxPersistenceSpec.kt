package hibernate

import TestSubject
import net.eraga.jpa.async.RxPersistence
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUtil
import kotlin.test.assertFails
import kotlin.test.assertTrue

/**
 * Date: 08/05/2018
 * Time: 18:16
 */
object RxPersistenceSpec : SubjectSpek<TestSubject>({
    subject { TestSubject("H2 Hibernate") }

    given("$subject persistence unit") {
        on("createEntityManagerFactory ") {
            it("should create with  with persistenceUnitName") {
                val emf = RxPersistence
                        .createEntityManagerFactory(subject.persistenceUnit)
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
                                subject.persistenceUnit,
                                properties
                        )
                        .blockingGet()

                val success = emf is EntityManagerFactory

                if (success) emf.close()

                assertTrue { success }
            }

            it("should fail with with wrong url in properties") {
                if (!(subject.persistenceUnit.contains("eclipselink", true)
                                || subject.persistenceUnit.contains("openjpa", true))) {
                    // Skip old EclipseLink // OpenJpa as they fail this test.
                    // Fuck legacy standards anyway.

                    assertFails {
                        val properties = Properties()
                        properties.setProperty("javax.persistence.jdbc.url", "test_url")

                        RxPersistence
                                .createEntityManagerFactory(
                                        subject.persistenceUnit,
                                        properties
                                )
                                .blockingGet()

                    }
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
