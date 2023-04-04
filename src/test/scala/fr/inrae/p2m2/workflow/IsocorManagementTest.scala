package fr.inrae.p2m2.workflow
import fr.inrae.p2m2.Data
import fr.inrae.p2m2.tools._
import utest.{TestSuite, Tests, test}

import scala.concurrent.ExecutionContext
import scala.scalajs.concurrent.QueueExecutionContext
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport


object IsocorManagementTest extends TestSuite {
  val exec: ExecutionContext = QueueExecutionContext.timeouts()

  def tests: Tests = Tests {
    test("run process galaxy file") {
      IsocorReader.getMeanEnrichmentByFragment(Data.contentGlycine)
      IsocorManagement.workflow(Data.contentGlycine)

    }
  }
}
