package fr.inrae.p2m2.tools

import utest.{TestSuite, Tests, test}
object StaticListArrangement extends TestSuite {
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

  }

}
