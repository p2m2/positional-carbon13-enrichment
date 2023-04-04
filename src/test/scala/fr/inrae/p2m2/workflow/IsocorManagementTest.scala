package fr.inrae.p2m2.workflow
import io.scalajs.nodejs.fs.Fs
import fr.inrae.p2m2.tools._
import utest.{TestSuite, Tests, test}

import scala.concurrent.ExecutionContext
import scala.scalajs.concurrent.QueueExecutionContext


object IsocorManagementTest extends TestSuite {
  val exec: ExecutionContext = QueueExecutionContext.timeouts()

  def tests: Tests = Tests {
    test("run process galaxy file") {
      Fs.readFileFuture("src/test/resources/glycine.test.tsv").map {
        case (buffer) =>
          val content = buffer.toString()
          val listMeanEnrichment = IsocorReader.getMeanEnrichmentByFragment(content)
          val r2 = IsocorManagement.workflow(content)
      }(exec)
    }
  }
}
