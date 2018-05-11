/**
 * Date: 11/05/2018
 * Time: 03:54
 */
class TestSubject(
        var persistenceUnit: String
) {
    var clazz: Class<*> = Book::class.java

    override fun toString(): String {
        return persistenceUnit
    }
}
