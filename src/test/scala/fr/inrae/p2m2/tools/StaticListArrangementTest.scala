package fr.inrae.p2m2.tools

import utest.{TestSuite, Tests, test}
object StaticListArrangementTest extends TestSuite {
  def tests: Tests = Tests {

    test("sumPossibilities [[a] [b]] -> [[a,b]]") {
      assert(ComputeCarbonMeanEnrichment.sumPossibilities(Seq(Seq("a"),Seq("b"))) == Seq(Seq("a","b")))
    }

    test("sumPossibilities [[a,b] [c]] -> [[a,c] [b,c]]") {
      assert(ComputeCarbonMeanEnrichment.sumPossibilities(Seq(Seq("a","b"), Seq("c"))) == Seq(Seq("a","c"), Seq( "b","c")))
    }

    test("sumPossibilities [[a,b] [c,d]] -> [[a,c] [a,d] [b,c] [b,d]]") {
      assert(ComputeCarbonMeanEnrichment.sumPossibilities(Seq(Seq("a", "b"), Seq("c","d")))
        == Seq(Seq("a", "c"), Seq("a", "d"),Seq("b", "c"), Seq("b", "d")))
    }

    test("sumPossibilities [[a,b] [c,d] [e]] -> [[a,c,e] [a,d,e] [b,c,e] [b,d,e]]") {
      assert(ComputeCarbonMeanEnrichment.sumPossibilities(Seq(Seq("a", "b"), Seq("c", "d"), Seq("e")))
        == Seq(Seq("a", "c","e"), Seq("a", "d","e"), Seq("b", "c","e"), Seq("b", "d","e")))
    }

    test("computeSingleValue on left term") {
      val m = Seq(
        "C1" -> Seq((0.5, Seq(""))),
        "C1C2" -> Seq((0.5, Seq(""))),
        "C2C3" -> Seq((0.5, Seq("116")), (0.5, Seq("190")))
      ).toMap

      val (p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)
      val res = ComputeCarbonMeanEnrichment.computeSingleValue("C1C3", p, r)
      assert(res.length == 2)

    }

    test("computeSingleValue on right term ") {
      val m = Seq(
        "C1" -> Seq((0.5, Seq(""))),
        "C1C2" -> Seq((0.5, Seq(""))),
        "C2C3" -> Seq((0.5, Seq("116")), (0.5, Seq("190")))
      ).toMap

      val (p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)
      //println(p)
      //println(r)
      val res2 = ComputeCarbonMeanEnrichment.computeSingleValue("C2", p, r)
     // println(res2)
      assert(res2.length == 1)
    }

    test("computeSingleValue on right term ") {
      val m = Seq(
        "C1" -> Seq((0.5, Seq(""))),
        "C1C2" -> Seq((0.5, Seq(""))),
        "C2C3" -> Seq((0.5, Seq("116")), (0.5, Seq("190")))
      ).toMap

      val (p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)
      //println(p)
      //println(r)
      val res2 = ComputeCarbonMeanEnrichment.computeSingleValue("C2", p, r)
      //println(res2)
      assert(res2.length == 1)
    }

    test(" Computed isotope of Glutamate") {
      val m = Seq(
        "C1" -> Seq((0.21314815075032625, Seq(""))),
        "C1C5" -> Seq((0.4933145028361162, Seq(""))),
        "C2C5" -> Seq((0.5050491093728191, Seq("m246")), (0.5189356622515641, Seq("m156")))
      ).toMap
      val (p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)
      val res2 = ComputeCarbonMeanEnrichment.computeSingleValue("C1", p, r)
      //println(res2)
      assert(res2.length == 2)
    }

  }

}
