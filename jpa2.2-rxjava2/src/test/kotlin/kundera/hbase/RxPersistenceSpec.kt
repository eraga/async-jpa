package kundera.hbase

import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

/**
 * Date: 08/05/2018
 * Time: 18:16
 */
object RxPersistenceSpec : SubjectSpek<String>({
    subject { "HBase Kundera" }

    itBehavesLike(kundera.neo4j.RxPersistenceSpec)
})
