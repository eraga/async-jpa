import net.eraga.rxjpa2.*
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.FlushModeType
import javax.persistence.Persistence

/**
 * Date: 09/05/2018
 * Time: 23:04
 */
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        lateinit var entityManager: EntityManager
        lateinit var entityManagerFactory: EntityManagerFactory

        println(2)
        val properties = Properties()
        entityManagerFactory = Persistence
                .createEntityManagerFactory("HBase Kundera", properties)
//                .blockingGet()
        println(2)

        entityManager = entityManagerFactory
                .rxCreateEntityManager()
                .blockingGet()
        println(2)
        entityManager
                .rxTransaction {
                    it.persist(Book("First book"))
                    it.persist(Book("Second book"))
                    it.persist(Book("Third book"))
                    it.persist(Book("Fourth book"))
                }
                .blockingGet()?.printStackTrace()
        println(2)

        println(entityManager.rxFind(Book::class.java, 1).blockingGet())

        entityManagerFactory.close()
    }
}
