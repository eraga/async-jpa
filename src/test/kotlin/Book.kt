import javax.persistence.*


/**
 * Date: 08/05/2018
 * Time: 18:24
 */
@Entity
data class Book(
    var title: String = ""
) {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
    @Column(name = "id")
    var id: Int? = null

    var text = ""
}
