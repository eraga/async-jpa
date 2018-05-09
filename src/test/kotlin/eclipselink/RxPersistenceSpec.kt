package eclipselink

import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

/**
 * Date: 08/05/2018
 * Time: 18:16
 */
object RxPersistenceSpec : SubjectSpek<String>({
    subject { "H2 EclipseLink" }

    itBehavesLike(hibernate.RxPersistenceSpec)
})
