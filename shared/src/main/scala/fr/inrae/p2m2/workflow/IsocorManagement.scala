package fr.inrae.p2m2.workflow

import fr.inrae.p2m2.tools._
case object IsocorManagement {

  def workflow(isocorContent : String): Map[(String,String,String), Map[String,Double]] = {
    val listMeanEnrichment = IsocorReader.getMeanEnrichmentByFragment(isocorContent)

    listMeanEnrichment
      .groupBy(x => (x.sample, x.derivative, x.metabolite))
   //   .take(3) // debugging.....
      .map {
        case (k, listValues: Seq[IsocorValue]) if listValues.distinct.size > 1 =>
          println(k, listValues.distinct.size)
          /* setting with experimental values     CX...CZ =>  value,FRAGMENT  ""*/
          val mapArrangementCarbon13: Map[String, Seq[(Double, Seq[String])]] =
            listValues
              .distinct
              .map {
                case isocorVal => isocorVal.code -> Seq((isocorVal.meanEnrichment, Seq(isocorVal.fragment)))
              }.toMap

          println("**************************")
          println(mapArrangementCarbon13)
          val (l, p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(mapArrangementCarbon13)
          println(l)
          println(p)
          println(r)
          val res = ComputeCarbonMeanEnrichment.computeValues(r, p, l)
          val res2 = ComputeCarbonMeanEnrichment.computeValues(res, p, l)
          val res3 = ComputeCarbonMeanEnrichment.computeValues(res2, p, l)
          ComputeCarbonMeanEnrichment.printRes(res3)
          k -> res3.flatMap( x => x._2.map( y => (x._1 + y.fragList.mkString("_"),y.mean)) )
        case (k, _) => println(k," => only 1 value") ;  k ->Map()
      }
    }
}