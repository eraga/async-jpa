import javax.persistence.*;

/**
 * Date: 11/05/2018
 * Time: 03:51
 */
@Entity
@Table(name = "JAVA_BOOKS")
public class JavaBook {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    private String title;
    private String text;

    public JavaBook() {

    }

    public JavaBook(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
