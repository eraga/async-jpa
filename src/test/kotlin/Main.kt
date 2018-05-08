import model.Book
import net.eraga.rxjpa2.RxPersistence
import net.eraga.rxjpa2.rxCreateEntityManager
import net.eraga.rxjpa2.rxResultList

/**
 * Date: 08/05/2018
 * Time: 19:31
 */
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val entityManagerFactory = RxPersistence
                .createEntityManagerFactory("rxJpa2-test")
                .blockingGet()

        val entityManager = entityManagerFactory
                .rxCreateEntityManager()
                .blockingGet()

        val firstResult = entityManager
                .createQuery(
                        "SELECT b From Book b where title = 'Unit Test Hibernate/JPA with in memory H2 Database'",
                        Book::class.java)
                .rxResultList()
                .blockingGet()

        println(firstResult)

        entityManagerFactory.close()
    }

}
