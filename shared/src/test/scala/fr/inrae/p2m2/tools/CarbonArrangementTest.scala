package fr.inrae.p2m2.tools

import utest.{TestSuite, Tests, test}

object CarbonArrangementTest extends TestSuite {
  def tests: Tests = Tests {

    test("get iteration p=1,n=3 => C1,C2,C3") {
      assert(CarbonArrangement.iteration(1, 3) == Seq("C1", "C2", "C3"))
    }

    test("get iteration p=2,n=3 => C1C2,C2C3") {
      assert(CarbonArrangement.iteration(2, 3) == Seq("C1C2", "C2C3"))
    }

    test("get iteration p=3,n=3 => C1C2C3") {
      assert(CarbonArrangement.iteration(3, 3) == Seq("C1C2C3"))
    }

    test("get iteration p=3,n=4 => C1C2C3,C2C3C4") {
      assert(CarbonArrangement.iteration(3, 4) == Seq("C1C2C3", "C2C3C4"))
    }

    test("test arrangement n=4 => C1,C2,C3,C4,C1C2,C2C3,C3C4,C1C2C3,C2C3C4,C1C2C3C4") {
      assert(CarbonArrangement.getArrangements(4) == Seq(
        "C1", "C2", "C3", "C4",
        "C1C2", "C2C3", "C3C4",
        "C1C2C3", "C2C3C4",
        "C1C2C3C4"))
    }

    test("diffMeanEnrichment C2_100 , C1C2 => C2_100") {
      assert(CarbonArrangement.diffMeanEnrichment((0.55, 2.0), Seq((0.4, 1.0)), 1) == 0.55 * 2 - 0.4)
    }

    test("sumMeanEnrichment C1C2_200 , C3C4_100 => C1C2C3C4_200_100") {
      assert(CarbonArrangement.sumMeanEnrichment(Seq((0.55, 2.0), (0.4, 2.0)), 4) == ((2 * 0.4) + (0.55 * 2)) / 4.0)
    }

    test("code2Indexes C1C5") {
      assert(CarbonArrangement.code2Indexes("C1C5") == Some(1, 5))
    }

    test("code2Indexes C1C0") {
      assert(CarbonArrangement.code2Indexes("C1C0").isEmpty)
    }

    test("code2Indexes C1C1") {
      assert(CarbonArrangement.code2Indexes("C1C1").isEmpty)
    }

    test("code2Indexes C1C12") {
      assert(CarbonArrangement.code2Indexes("C1C12") == Some(1, 12))
    }

    test("code2Indexes C1") {
      assert(CarbonArrangement.code2Indexes("C1") == Some(1, 1))
    }

    test("indexes2code 1,1 => C1") {
      assert(CarbonArrangement.indexes2code(Some(1, 1)).contains("C1"))
    }

    test("indexes2code 1,0 => None") {
      assert(CarbonArrangement.indexes2code(Some(1, 0)).isEmpty)
    }

    test("indexes2code 1,4 => C1C4") {
      assert(CarbonArrangement.indexes2code(Some(1, 4)).contains("C1C4"))
    }

    test("planning C1") {
      assert(CarbonArrangement.planningComputedAdditionalValues("C1") == Seq("C1" -> Seq("C1")))
    }

    test("planning C1C3") {
      val res = CarbonArrangement.planningComputedAdditionalValues("C1C3")
      assert(res.contains("C1C3" -> Seq("C1", "C2", "C3")))
      assert(res.contains("C1C3" -> Seq("C1C2", "C3")))
      assert(res.contains("C1C3" -> Seq("C1", "C2C3")))
      assert(res.size == 3)
    }

    test("planning C2C6 - 30 arrangements") {
      val res = CarbonArrangement.planningComputedAdditionalValues("C1C6")
      assert(res.size == 30)
    }

  }
}
