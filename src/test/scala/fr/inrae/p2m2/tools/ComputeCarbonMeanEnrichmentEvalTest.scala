package fr.inrae.p2m2.tools

import utest.{TestSuite, Tests, test}

object ComputeCarbonMeanEnrichmentEvalTest extends TestSuite {
  def tests: Tests = Tests {

    test("eval ex1 Alanine 2TMS") {
      val m = Seq(
        "C1" -> Seq((0.45, Seq())),
        "C2" -> Seq((0.55, Seq()))
      ).toMap

      val (l, p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)
      val res = ComputeCarbonMeanEnrichment.computeValues(r, p, l)
      println("== res ==")
      ComputeCarbonMeanEnrichment.printRes(res)
    }

    test("eval ex1 Alanine 2TMS") {
      val m = Seq(
        "C1" -> Seq((0.45, Seq())),
        "C1C2" -> Seq((0.5, Seq()))
      ).toMap

      val (l, p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)
      val res = ComputeCarbonMeanEnrichment.computeValues(r, p, l)
      println("== res ==")
      ComputeCarbonMeanEnrichment.printRes(res)
    }

    test("eval ex1 Alanine 2TMS") {

      val m = Seq(
        "C1" -> Seq((0.45, Seq())),
        "C1C2" -> Seq((0.55, Seq())),
        "C2C3" -> Seq((0.46, Seq("116")), (0.48, Seq("190"))),
      ).toMap

      val (l, p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)

      println("================================ STEP 0 ==")

      println("========================== EXECUTION PLAN ================================")
      println(p.map(x => x._1 + "::" + x._2.mkString(",")).mkString("\n"))
      ComputeCarbonMeanEnrichment.printRes(r)

      val res = ComputeCarbonMeanEnrichment.computeValues(r, p, l)
      val res2 = ComputeCarbonMeanEnrichment.computeValues(res, p, l)
      val res3 = ComputeCarbonMeanEnrichment.computeValues(res2, p, l)
      val res4 = ComputeCarbonMeanEnrichment.computeValues(res3, p, l)

      ComputeCarbonMeanEnrichment.printRes(res4)
      val res5 = ComputeCarbonMeanEnrichment.computeValues(res4, p, l)
      assert(res5 == res4)
    }
  }
}
