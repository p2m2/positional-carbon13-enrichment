package fr.inrae.p2m2.tools

import scala.collection.immutable.Seq
import utest.{TestSuite, Tests, test}

object ComputeCarbonMeanEnrichmentTest extends TestSuite {
  def tests: Tests = Tests {

    test("computeValues None") {
      val m = Seq(
        "C1C2" -> Seq(),
        "C3" -> Seq(),
        "C1" -> Seq(),
        "C2C3" -> Seq(),
        "C1C3" -> Seq()
      ).toMap

      val (l, p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)
      val res = ComputeCarbonMeanEnrichment.computeValues(r, p, l)

      assert(res.count { case (k, v) => v.isEmpty } == res.size)
    }

    test("computeValues ADD C1C3 -> [C1C2, C3] ou [C1, C2C3]") {
      val m =
        Seq(
            "C1C2" -> Seq((0.5, Seq("FRAG1"))),
            "C3" -> Seq((0.5, Seq("FRAG1"))),
            "C1" -> Seq((0.5, Seq("FRAG1"))),
            "C2C3" -> Seq((0.5, Seq("FRAG1"))),
            "C1C3" -> Seq()
          ).toMap

      val (l, p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)
      val res = ComputeCarbonMeanEnrichment.computeValues(r, p, l)
      assert(res.contains("C1C3"))
      assert(res("C1C3").nonEmpty)
    }


    test("computeValues ADD C1 = 3*C1C3 - 2*C2C3") {

      val m = Seq(
        "C1C2" -> Seq((0.5, Seq("FRAG1"))),
        "C3" -> Seq((0.5, Seq("FRAG1"))),
        "C1" -> Seq(),
        "C2C3" -> Seq((0.5, Seq("FRAG1"))),
        "C1C3" -> Seq((0.5, Seq("FRAG1")))
      ).toMap

      val (l, p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)
      val res = ComputeCarbonMeanEnrichment.computeValues(r, p, l)

      assert(res.contains("C1"))
      assert(res("C1").nonEmpty)
    }

    test("listSumValuesPossibilities") {
      // FRAG1, FRAG3
      // FRAG1, FRAG4
      // FRAG2, FRAG3
      // FRAG2, FRAG4
      val m =
      Seq(
          "C1C2" -> Seq((0.5, Seq("FRAG1")), (0.5, Seq("FRAG2"))),
          "C3" -> Seq((0.5, Seq("FRAG3")), (0.5, Seq("FRAG4")))
        ).toMap

      val (l, p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)
      val res = ComputeCarbonMeanEnrichment.computeValues(r, p, l)
      assert(res.size == 6)
    }

    test("computeValues ADD C1 = 3*C1C3 - 2*C2C3 - 3*2=6 possibilities") {
      val m = Seq(
        "C1C2" -> Seq(),
        "C3" -> Seq(),
        "C1" -> Seq(),
        "C2C3" -> Seq((0.5, Seq("FRAG1")), (0.5, Seq("FRAG2")), (0.5, Seq("FRAG3"))),
        "C1C3" -> Seq((0.5, Seq("FRAG1")), (0.5, Seq("FRAG2")))
      ).toMap

      val (l, p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)
      val res = ComputeCarbonMeanEnrichment.computeValues(r, p, l)

      val res2 = ComputeCarbonMeanEnrichment.computeValues(res, p, l)

      assert(res2.contains("C1"))
      assert(res2("C1").size == 5)
    }

  }

}
