import net.eraga.rxjpa2.*
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.FlushModeType

/**
 * Date: 09/05/2018
 * Time: 23:04
 */
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        lateinit var entityManager: EntityManager
        lateinit var entityManagerFactory: EntityManagerFactory

        val properties = Properties()
        entityManagerFactory = RxPersistence
//                .createEntityManagerFactory("H2 Hibernate", properties)
                .createEntityManagerFactory("H2 Kundera", properties)
                .blockingGet()

        entityManager = entityManagerFactory
                .rxCreateEntityManager()
                .blockingGet().apply {
                    flushMode = FlushModeType.COMMIT
                }

        println(entityManager.flushMode)

//        entityManager
//                .rxTransaction {
//                    it.persist(Book("First book"))
//                    it.persist(Book( "Second book"))
//                    it.persist(Neo4jBook(3, "Third book"))
//                    it.persist(Neo4jBook(4, "Fourth book"))
//                }
//                .blockingGet()?.printStackTrace()
        val book = Book("rxPersistBook")

        val error = entityManager
                .rxPersist(book)
                .blockingGet()



        if (error != null)
            throw error

        book.text = "persisted text"
//        entityManager.transaction.begin()
//        entityManager.flush()
//        entityManager.transaction.commit()


//        val bookNew = entityManager
//                .rxFind(Book::class.java, 1)
//                .blockingGet()
//
//        println(bookNew)
        entityManagerFactory.close()
    }
}
