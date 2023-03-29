package fr.inrae.p2m2.tools

import utest.{TestSuite, Tests, test}
object StaticListArrangementTest extends TestSuite {
  def tests: Tests = Tests {
    test("test test") {
      val m = Seq(
        "C1" -> Seq((0.5,Seq(""))),
        "C1C2" -> Seq((0.5,Seq(""))),
        "C2C3" -> Seq((0.5, Seq("116")), (0.5, Seq("190")))
      ).toMap

      val (p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)
      println(p)
      println(r)
      ComputeCarbonMeanEnrichment.computeSingleValue("C1C3",p,r)
      //ComputeCarbonMeanEnrichment.computeSingleValue("C3",p,r)
      /* check */
      //"C1C3 [m278]"
      //assert( p.exists(_._1 == "C1C3") )
    }

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

  }

}
