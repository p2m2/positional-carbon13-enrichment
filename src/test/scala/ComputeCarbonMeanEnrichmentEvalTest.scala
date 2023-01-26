package fr.inrae.p2m2.tools

import utest.{TestSuite, Tests, test}

object ComputeCarbonMeanEnrichmentEvalTest extends TestSuite {
  def tests: Tests = Tests {
    test("eval ex1 Alanine 2TMS") {
      val m = Seq(
        "C1" -> Seq((0.45, "")),
        "C1C2" -> Seq((0.55, "")),
        "C2C3" -> Seq((0.46, "116"), (0.48, "190"), (0.42, "290")),
      ).toMap

      val res = ComputeCarbonMeanEnrichment.eval(m)

      println(res)
    }
  }
}
