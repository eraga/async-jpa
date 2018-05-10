package kundera.neo4j

import com.impetus.kundera.PersistenceProperties
import com.impetus.kundera.metadata.KunderaMetadataManager
import com.impetus.kundera.persistence.EntityManagerFactoryImpl
import net.eraga.rxjpa2.RxPersistence
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.neo4j.kernel.impl.util.FileUtils
import java.io.File
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
    subject { "neo4j Kundera" }

    given("$subject persistence unit") {
        on("createEntityManagerFactory ") {
            it("should create with  with persistenceUnitName") {
                val emf = RxPersistence
                        .createEntityManagerFactory(subject)
                        .blockingGet()

                val success = emf is EntityManagerFactory

                if (success) emf.closeAndDeleteDBFiles(subject)

                assertTrue { success }
            }

            it("should create with properties") {

                val properties = Properties()
                properties.setProperty("javax.persistence.jdbc.url", "jdbc:h2:mem:test")

                val emf = RxPersistence
                        .createEntityManagerFactory(
                                subject,
                                properties
                        )
                        .blockingGet()

                val success = emf is EntityManagerFactory

                if (success) emf.closeAndDeleteDBFiles(subject)

                assertTrue { success }
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

fun EntityManagerFactory.closeAndDeleteDBFiles(persistenceUnit: String) {
    this.close()
    val puMetadata = KunderaMetadataManager.getPersistenceUnitMetadata((this as EntityManagerFactoryImpl)
            .kunderaMetadataInstance, persistenceUnit)
    val datastoreFilePath = puMetadata.getProperty(PersistenceProperties.KUNDERA_DATASTORE_FILE_PATH)


    if (datastoreFilePath != null) {
        FileUtils.deleteRecursively(File(datastoreFilePath))
    }
}
