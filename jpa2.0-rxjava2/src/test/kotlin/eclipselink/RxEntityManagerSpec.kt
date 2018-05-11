package eclipselink

import TestSubject
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

/**
 * Date: 08/05/2018
 * Time: 21:23
 */
object RxEntityManagerSpec : SubjectSpek<TestSubject>({
    subject { TestSubject("H2 EclipseLink") }

    itBehavesLike(hibernate.RxEntityManagerSpec)
})
