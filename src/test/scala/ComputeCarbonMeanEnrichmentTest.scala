package fr.inrae.p2m2.tools

import utest.{TestSuite, Tests, test}

import scala.collection.immutable.Seq

object ComputeCarbonMeanEnrichmentTest extends TestSuite {
  def tests: Tests = Tests {
    test("computeValues None") {
      val entry = Seq(
        "C1C2" -> Seq(),
        "C3" -> Seq(),
        "C1" -> Seq(),
        "C2C3" -> Seq(),
        "C1C3" -> Seq()
      ).toMap
      val v = ComputeCarbonMeanEnrichment.computeValues(
        meanEnrichment = entry,
        executionPlan =
          Seq(
            Seq("C1", "C2", "C3"), Seq("C1C2", "C3"), Seq("C1", "C2C3")
          ),
        longestCodeCarbon = "C1C3"
      )
      assert(v.count { case (k, v) => v.isEmpty } == entry.size)
    }

    test("computeValues ADD C1C3 -> [C1C2, C3] ou [C1, C2C3]") {
      val res = ComputeCarbonMeanEnrichment.computeValues(
        meanEnrichment =
          Seq(
            "C1C2" -> Seq((0.5, Seq("FRAG1"))),
            "C3" -> Seq((0.5, Seq("FRAG1"))),
            "C1" -> Seq((0.5, Seq("FRAG1"))),
            "C2C3" -> Seq((0.5, Seq("FRAG1"))),
            "C1C3" -> Seq()
          ).toMap,
        executionPlan =
          Seq(
            Seq("C1", "C2", "C3"), Seq("C1C2", "C3"), Seq("C1", "C2C3")
          ),
        longestCodeCarbon = "C1C3"
      )
      println(res)
      assert(res.contains("C1C3"))
      assert(res("C1C3").nonEmpty)
    }


    test("computeValues ADD C1 = 3*C1C3 - 2*C2C3") {
      val res = ComputeCarbonMeanEnrichment.computeValues(
        meanEnrichment =
          Seq(
            "C1C2" -> Seq((0.5, Seq("FRAG1"))),
            "C3" -> Seq((0.5, Seq("FRAG1"))),
            "C1" -> Seq(),
            "C2C3" -> Seq((0.5, Seq("FRAG1"))),
            "C1C3" -> Seq((0.5, Seq("FRAG1")))
          ).toMap,
        executionPlan =
          Seq(
            Seq("C1", "C2", "C3"), Seq("C1C2", "C3"), Seq("C1", "C2C3")
          ),
        longestCodeCarbon = "C1C3"
      )
      println("========")
      println(res)
      assert(res.contains("C1"))
      assert(res("C1").nonEmpty)
    }

    test("listSumValuesPossibilities") {
      // FRAG1, FRAG3
      // FRAG1, FRAG4
      // FRAG2, FRAG3
      // FRAG2, FRAG4
      val res = ComputeCarbonMeanEnrichment.listSumValuesPossibilities(
        Seq(
          "C1C2" -> Seq((0.5, Seq("FRAG1")), (0.5, Seq("FRAG2"))),
          "C3" -> Seq((0.5, Seq("FRAG3")), (0.5, Seq("FRAG4")))
        ).toMap
      )
      println(res)
      assert(res.size == 4)
    }

    test("computeValues ADD C1 = 3*C1C3 - 2*C2C3 - 3*2=6 possibilities") {
      val m = Seq(
        "C1C2" -> Seq(),
        "C3" -> Seq(),
        "C1" -> Seq(),
        "C2C3" -> Seq((0.5, Seq("FRAG1")), (0.5, Seq("FRAG2")), (0.5, Seq("FRAG3"))),
        "C1C3" -> Seq((0.5, Seq("FRAG1")), (0.5, Seq("FRAG2")))
      ).toMap
      val res = ComputeCarbonMeanEnrichment.computeValues(
        meanEnrichment = m,
        executionPlan =
          Seq(
            Seq("C1", "C2", "C3"), Seq("C1C2", "C3"), Seq("C1", "C2C3")
          ),
        longestCodeCarbon = "C1C3"
      )

      ComputeCarbonMeanEnrichment.computeValues(
        meanEnrichment = res,
        executionPlan =
          Seq(
            Seq("C1", "C2", "C3"), Seq("C1C2", "C3"), Seq("C1", "C2C3")
          ),
        longestCodeCarbon = "C1C3"
      )

      assert(res.contains("C1"))
      assert(res("C1").size == 6)
    }

    /*

    test("computeValuesRecursive") {
      val m = Seq(
        "C1C2" -> Seq(),
        "C3" -> Seq(),
        "C1" -> Seq(),
        "C2C3" -> Seq((0.5, Seq("FRAG1")), (0.5, Seq("FRAG2")), (0.5, Seq("FRAG3"))),
        "C1C3" -> Seq((0.5, Seq("FRAG1")), (0.5, Seq("FRAG2")))
      ).toMap

      val res = ComputeCarbonMeanEnrichment.computeValuesRecursive(
        m,
        executionPlan =
          Seq(
            Seq("C1", "C2", "C3"), Seq("C1C2", "C3"), Seq("C1", "C2C3")
          ),
        longestCodeCarbon = "C1C3"
      )

      assert(res("C1").size == 6)
    }*/

  }

}
