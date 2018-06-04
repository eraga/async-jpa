package kundera.hbase

import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

/**
 * Date: 08/05/2018
 * Time: 22:39
 */
object RxEntityManagerFactorySpec : SubjectSpek<String>({
    subject { "HBase Kundera" }

    itBehavesLike(kundera.neo4j.RxEntityManagerFactorySpec)
})
