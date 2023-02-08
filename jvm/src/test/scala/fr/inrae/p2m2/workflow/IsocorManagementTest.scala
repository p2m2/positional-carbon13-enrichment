package fr.inrae.p2m2.workflow
import fr.inrae.p2m2.tools._
import utest.{TestSuite, Tests, test}

import scala.io.Source

object IsocorManagementTest extends TestSuite {
  def tests: Tests = Tests {
    test("run process galaxy file") {
      Source.fromResource("glycine.test.tsv") match {
        case null => println(" -- no resource --")
        case r =>
          val content : String = r.mkString
          println(content)
          val listMeanEnrichment = IsocorReader.getMeanEnrichmentByFragment(content)
          println(listMeanEnrichment)
          val r2 = IsocorManagement.workflow(content)
          println(r2)
      }

    }


  }
}
