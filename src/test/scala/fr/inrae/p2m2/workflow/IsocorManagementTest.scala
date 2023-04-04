package fr.inrae.p2m2.workflow
import fr.inrae.p2m2.tools._
import utest.{TestSuite, Tests, test}

import scala.concurrent.ExecutionContext
import scala.scalajs.concurrent.QueueExecutionContext
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport


@js.native
@JSImport("fs", "fs")
object fs extends js.Object {
  def readFileSync(path: String): String = js.native
}

object IsocorManagementTest extends TestSuite {
  val exec: ExecutionContext = QueueExecutionContext.timeouts()

  def readFile(name: String): String = {
    fs.readFileSync(name).toString
  }
  def tests: Tests = Tests {
    test("run process galaxy file") {
      val content = readFile("src/test/resources/glycine.test.tsv")
      IsocorReader.getMeanEnrichmentByFragment(content)
      IsocorManagement.workflow(content)
/*
      Fs.readFileFuture("src/test/resources/glycine.test.tsv").map {
        case (buffer) =>
          val content = buffer.toString()
          val listMeanEnrichment = IsocorReader.getMeanEnrichmentByFragment(content)
          val r2 = IsocorManagement.workflow(content)
      }(exec) */
    }
  }
}
