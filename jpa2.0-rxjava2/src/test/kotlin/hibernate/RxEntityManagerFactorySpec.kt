package hibernate

import TestSubject
import net.eraga.jpa.async.*
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import kotlin.test.assertEquals

/**
 * Date: 08/05/2018
 * Time: 22:39
 */
object RxEntityManagerFactorySpec : SubjectSpek<TestSubject>({
    System.setProperty("org.jboss.logging.provider", "slf4j")

    subject { TestSubject("H2 Hibernate") }

    var entityManager: EntityManager? = null
    lateinit var entityManagerFactory: EntityManagerFactory


    given("$subject persistence unit") {
        beforeEachTest {
            entityManagerFactory = RxPersistence
                    .createEntityManagerFactory(subject.persistenceUnit)
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
                properties["org.hibernate.flushMode"] = "COMMIT"
                entityManager = entityManagerFactory
                        .rxCreateEntityManager(properties)
                        .blockingGet()
                assertEquals("COMMIT", entityManager?.properties?.get("org.hibernate.flushMode"))
            }
        }
    }
})
