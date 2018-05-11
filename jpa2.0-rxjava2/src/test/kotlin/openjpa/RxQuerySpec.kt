package openjpa

import TestSubject
import JavaBook
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

/**
 * Date: 09/05/2018
 * Time: 18:22
 */
object RxQuerySpec : SubjectSpek<TestSubject>({
    subject { TestSubject("H2 OpenJpa").apply { clazz = JavaBook::class.java } }

//    itBehavesLike(hibernate.RxQuerySpec)
})
