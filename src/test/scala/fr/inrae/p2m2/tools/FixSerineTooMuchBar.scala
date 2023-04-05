package fr.inrae.p2m2.tools

import utest.{TestSuite, Tests, test}

/* fix bug with too much C2_m204 produced */
object FixSerineTooMuchBar extends TestSuite {

  def tests: Tests = Tests {
    val m = Seq(
      "C1C2" -> Seq((0.5229293502028365, Seq(""))),
      "C2C3" -> Seq((0.48441859048103897, Seq("m204")),(0.506150883956903, Seq("m278"))),
      "C1C3" -> Seq((0.5574118261363693, Seq())),
      "C2" -> Seq((0.5100469893183011, Seq())),
    ).toMap

    val (p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(m)
    val res = ComputeCarbonMeanEnrichment.computeValues(r, p)
    val res2 = ComputeCarbonMeanEnrichment.computeValues(res, p)
    res2.filter(_._1 == "C2").values.foreach(
      aa => aa.foreach(
        bb => println(bb)
      )
    )
  }

}
