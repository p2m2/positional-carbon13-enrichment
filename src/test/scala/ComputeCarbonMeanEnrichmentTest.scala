package fr.inrae.p2m2.tools

import utest.{TestSuite, Tests, test}

object ComputeCarbonMeanEnrichmentTest extends TestSuite {
  def tests: Tests = Tests {
    test("computeValues None") {
      assert(!ComputeCarbonMeanEnrichment.computeValues(
        meanEnrichment =
          Seq(
            "C1C2" -> None,
            "C3" -> None,
            "C1" -> None,
            "C2C3" -> None,
            "C1C3" -> None
          ).toMap,
        executionPlan =
          Seq(
            Seq("C1", "C2", "C3"), Seq("C1C2", "C3"), Seq("C1", "C2C3")
          ),
        longestCodeCarbon = "C1C3"
      ).exists { case (k, v) => v.isDefined })
    }

    test("computeValues ADD C1C3 -> [C1C2, C3] ou [C1, C2C3]") {
      val res = ComputeCarbonMeanEnrichment.computeValues(
        meanEnrichment =
          Seq(
            "C1C2" -> Some((0.5, "FRAG1")),
            "C3" -> Some((0.5, "FRAG1")),
            "C1" -> Some((0.5, "FRAG1")),
            "C2C3" -> Some((0.5, "FRAG1")),
            "C1C3" -> None
          ).toMap,
        executionPlan =
          Seq(
            Seq("C1", "C2", "C3"), Seq("C1C2", "C3"), Seq("C1", "C2C3")
          ),
        longestCodeCarbon = "C1C3"
      )
      assert(res.contains("C1C3"))
      assert(res("C1C3").isDefined)
    }

    test("computeValues ADD C1 = 3*C1C3 - 2*C2C3") {
      val res = ComputeCarbonMeanEnrichment.computeValues(
        meanEnrichment =
          Seq(
            "C1C2" -> Some((0.5, "FRAG1")),
            "C3" -> Some((0.5, "FRAG1")),
            "C1" -> None,
            "C2C3" -> Some((0.5, "FRAG1")),
            "C1C3" -> Some((0.5, "FRAG1"))
          ).toMap,
        executionPlan =
          Seq(
            Seq("C1", "C2", "C3"), Seq("C1C2", "C3"), Seq("C1", "C2C3")
          ),
        longestCodeCarbon = "C1C3"
      )
      assert(res.contains("C1"))
      assert(res("C1").isDefined)
    }

  }

}
