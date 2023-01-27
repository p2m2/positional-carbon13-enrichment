package fr.inrae.p2m2.tools

import utest.{TestSuite, Tests, test}

object ComputeCarbonMeanEnrichmentEvalTest extends TestSuite {
  def tests: Tests = Tests {
    test("eval ex1 Alanine 2TMS") {
      val m = Seq(
        "C1" -> Seq((0.45, Seq())),
        "C1C2" -> Seq((0.55, Seq())),
        "C2C3" -> Seq((0.46, Seq("116")), (0.48, Seq("190")), (0.42, Seq("290"))),
      ).toMap

      val res = ComputeCarbonMeanEnrichment.eval(m)
      println("== res ==")
      ComputeCarbonMeanEnrichment.printRes2(res)
      val res2 = ComputeCarbonMeanEnrichment.eval(res)
      println("== res 2==")
      ComputeCarbonMeanEnrichment.printRes2(res2)
    }
  }
}
