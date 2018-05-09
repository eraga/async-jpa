package eclipselink

import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

/**
 * Date: 08/05/2018
 * Time: 21:23
 */
object RxEntityManagerSpec : SubjectSpek<String>({
    subject { "H2 EclipseLink" }

    itBehavesLike(hibernate.RxEntityManagerSpec)
})
