package fr.inrae.p2m2.tools

import utest.{TestSuite, Tests, test}

object ComputeCarbonMeanEnrichmentEvalTest extends TestSuite {
  def tests: Tests = Tests {
    /*
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
*/
    test("eval ex1 Alanine 2TMS") {

      val m = Seq(
        "C1" -> Seq((0.45, Seq())),
        "C1C2" -> Seq((0.55, Seq())),
        "C2C3" -> Seq((0.46, Seq("116")), (0.48, Seq("190")), (0.42, Seq("290"))),
      ).toMap
/*
      val m = Seq(
        "C1" -> Seq((0.45, Seq())),
        "C2C3" -> Seq((0.55, Seq())),
        "C2" -> Seq((0.46, Seq())), //(0.46, Seq("200"),(0.42, Seq("100")
      ).toMap
*/
      val (l,p,r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)

      println("================================ STEP 0 ==")

      println("========================== EXECUTION PLAN ================================")
      println(p.map( x => x._1+"::"+x._2.mkString(",")).mkString("\n"))
      ComputeCarbonMeanEnrichment.printRes(r)

      val res = ComputeCarbonMeanEnrichment.computeValues(r,p,l)
      println("================================ STEP 1 ==")
      ComputeCarbonMeanEnrichment.printRes(res)

      val res2 = ComputeCarbonMeanEnrichment.computeValues(res,p,l)
      println("================================ STEP 2 ==")
      ComputeCarbonMeanEnrichment.printRes(res2)
      println(res==res2)
/*
      val res3 = ComputeCarbonMeanEnrichment.computeValues(res2, p, l)
      println("== res 3==")
      ComputeCarbonMeanEnrichment.printRes(res3)
      println(res2==res3)

      val res4 = ComputeCarbonMeanEnrichment.computeValues(res3, p, l)
      println("== res 4==")
      ComputeCarbonMeanEnrichment.printRes(res4)
      println(res3 == res4)
*/

    }
  }
}
