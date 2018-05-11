package openjpa

import TestSubject
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

/**
 * Date: 08/05/2018
 * Time: 18:16
 */
object RxPersistenceSpec : SubjectSpek<TestSubject>({
    subject { TestSubject("H2 OpenJpa") }

//    itBehavesLike(hibernate.RxPersistenceSpec)
})
