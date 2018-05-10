import kundera.neo4j.closeAndDeleteDBFiles
import net.eraga.rxjpa2.*
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
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
//        val persistenceUnit = "neo4j Kundera"
        val persistenceUnit = "HBase Kundera"

        println(2)
        val properties = Properties()
        entityManagerFactory = Persistence
                .createEntityManagerFactory(persistenceUnit, properties)
//                .blockingGet()
        println(2)

        entityManager = entityManagerFactory
                .rxCreateEntityManager()
                .blockingGet()
//        println(2)
        entityManager
                .rxTransaction {
                    it.persist(Neo4jBook("First book", 1))
                    it.persist(Neo4jBook("Second book"))
                    it.persist(Neo4jBook("Third book"))
                    it.persist(Neo4jBook("Fourth book", 39, num = 0))
                }
                .blockingGet()?.printStackTrace()
//        println(2)

//        entityManager.getTransaction().begin();
//        entityManager.persist(Neo4jBook("Good book", text = "Long text", id = 1));
//        entityManager.persist(Neo4jBook("Not book"));
//        entityManager.persist(Neo4jBook("Bad book"));
//        entityManager.getTransaction().commit();

        entityManager.rxFlush()
                .blockingGet()


        entityManager
                .rxFind(Neo4jBook::class.java, 39)
                .subscribe({
                    println(it)
                },{
                    it.printStackTrace()
                })

        val cb = entityManager.criteriaBuilder

        val cq = cb
                .createQuery(Neo4jBook::class.java)

        val from = cq.from(Neo4jBook::class.java)
        cq
                .select(from)
                .where(cb.equal(from.get<String>("title"), "%book"))

        entityManager
                .createQuery(
                        "SELECT b From Neo4jBook b"
                )
                .rxResultList()
                .subscribe ({
                    println("All Books:\n" + it.toString())
                }, {
                    it.printStackTrace()
                })


        entityManager
//                .criteriaBuilder
//                .selectCase<Neo4jBook>()
                .createQuery(
                        "SELECT b From Neo4jBook b where b.num >= :id"
//                        Neo4jBook::class.java
                )
                .setParameter("id", 1)
                .rxResultList()
                .subscribe ({
                    println("id > 0 " + it.toString())
                }, {
                    it.printStackTrace()
                })



        entityManager
                .createQuery(
                        "SELECT b From Neo4jBook b where b.title like :title",
                        Neo4jBook::class.java
                )
                .setParameter("title", "%d b%")
                .rxResultList()
                .subscribe ({
                    println("%d n%: " + it)
                }, {
                    println("Error!:")
                    it.printStackTrace()
                })


        Thread.sleep(3000)
        entityManagerFactory.close()
    }
}
