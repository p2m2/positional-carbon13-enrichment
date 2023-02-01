package fr.inrae.p2m2.tools

import utest.{TestSuite, Tests, test}

import java.io.File

object IsocorReaderTest extends TestSuite {

  def tests: Tests = Tests {
    test("extractInformatiponMetaboliteName") {
      val Some((metaboliteName, carbonStart, carbonEnd, fragmentName))
      = IsocorReader.extractInformatiponMetaboliteName("AlphaalanineC2C3m116")

      assert(metaboliteName == "Alphaalanine")
      assert(carbonStart == 2)
      assert(carbonEnd.contains(3))
      assert(fragmentName == "m116")
    }
/*
    test("test galaxy isocor output") {
      val lRes = IsocorReader.getMeanEnrichmentByFragment(new File(getClass.getResource("/galaxy430_res.tsv").getPath))
      assert(lRes.nonEmpty)
    }
*/
  }
}
