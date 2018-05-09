import javax.persistence.*


/**
 * Date: 08/05/2018
 * Time: 18:24
 */
@Entity
data class Neo4jBook(
        var title: String = "",
        @Id
        @Column(name = "id")
        var id: Int = Math.random().toInt()
) {


    var text = ""
}
