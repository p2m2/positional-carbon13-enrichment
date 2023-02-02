package fr.inrae.p2m2.app

import fr.inrae.p2m2.app.PositionalCarbon13EnrichmentMain.{Config, process}
import utest.{TestSuite, Tests, test}
import fr.inrae.p2m2.workflow.IsocorManagement

import java.io.File

object PositionalCarbon13EnrichmentTest extends TestSuite {

  def tests: Tests = Tests {
    test("run process galaxy file") {
      process(Config(Seq(new File(getClass.getResource("/galaxy430_res.tsv").getPath))))
    }


  }
}
