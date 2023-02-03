package fr.inrae.p2m2.tools

import utest.{TestSuite, Tests, test}

object ComputeCarbonMeanEnrichmentEvalTest extends TestSuite {
  def tests: Tests = Tests {

    test("eval ex1 Alanine 2TMS") {
      val m = Seq(
        "C1" -> Seq((0.45, Seq())),
        "C2" -> Seq((0.55, Seq()))
      ).toMap

      val (p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)
      val res = ComputeCarbonMeanEnrichment.computeValues(r, p)
      ComputeCarbonMeanEnrichment.printRes(res)
      val res3 = ComputeCarbonMeanEnrichment.computeValues(res, p)
      // stabilité avec 1 iteration
      assert(res==res3)
    }

    test("eval ex1 Alanine 2TMS") {
      val m = Seq(
        "C1" -> Seq((0.45, Seq())),
        "C1C2" -> Seq((0.5, Seq()))
      ).toMap

      val (p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)
      val res = ComputeCarbonMeanEnrichment.computeValues(r, p)
      println("== res ==")
      ComputeCarbonMeanEnrichment.printRes(res)
      val res3 = ComputeCarbonMeanEnrichment.computeValues(res, p)
      // stabilité avec 1 iteration
      assert(res == res3)
    }

    test("eval ex1 Alanine 2TMS") {

      val m = Seq(
        "C1" -> Seq((0.45, Seq())),
        "C1C2" -> Seq((0.55, Seq())),
        "C2C3" -> Seq((0.46, Seq("116")), (0.48, Seq("190"))),
      ).toMap

      val (p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)

      println("================================ STEP 0 ==")

      println("========================== EXECUTION PLAN ================================")
      println(p.map(x => x._1 + "::" + x._2.mkString(",")).mkString("\n"))
      ComputeCarbonMeanEnrichment.printRes(r)

      val res = ComputeCarbonMeanEnrichment.computeValues(r, p)
      val res2 = ComputeCarbonMeanEnrichment.computeValues(res, p)
      val res3 = ComputeCarbonMeanEnrichment.computeValues(res2, p)
      val res4 = ComputeCarbonMeanEnrichment.computeValues(res3, p)

      ComputeCarbonMeanEnrichment.printRes(res4)
      val res5 = ComputeCarbonMeanEnrichment.computeValues(res4, p)
      assert(res5 == res4)
    }
  }
}
