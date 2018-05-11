package eclipselink

import TestSubject
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

/**
 * Date: 09/05/2018
 * Time: 18:22
 */
object RxQuerySpec : SubjectSpek<TestSubject>({
    subject { TestSubject("H2 EclipseLink") }

    itBehavesLike(hibernate.RxQuerySpec)
})
