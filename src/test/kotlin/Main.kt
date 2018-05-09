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
                .createEntityManagerFactory("rxJpa2-test-eclipse")
                .blockingGet()

        val entityManager = entityManagerFactory
                .rxCreateEntityManager()
                .blockingGet()

        val firstResult = entityManager
                .createQuery(
                        "SELECT b From Book b where b.title = 'Unit Test'",
                        Book::class.java)
                .rxResultList()
                .blockingGet()

        println(firstResult)

        entityManagerFactory.close()
    }

}
