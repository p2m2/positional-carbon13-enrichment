package fr.inrae.p2m2.workflow

import fr.inrae.p2m2.tools._
case object IsocorManagement {

  def workflow(isocorContent : String): Map[(String,String), Map[String,Double]] = {
    val listMeanEnrichment = IsocorReader.getMeanEnrichmentByFragment(isocorContent)

    listMeanEnrichment
      .groupBy(x => (x.sample, x.metabolite))
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
              }.groupBy(_._1).map( x => x._1 -> x._2.flatMap(_._2))

          println("================mapArrangementCarbon13============")
          println(mapArrangementCarbon13)
          println("============================")
          val (p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(mapArrangementCarbon13)

          val res = ComputeCarbonMeanEnrichment.computeValues(r, p)
          val res2 = ComputeCarbonMeanEnrichment.computeValues(res, p)
          val res3 = ComputeCarbonMeanEnrichment.computeValues(res2, p)
          ComputeCarbonMeanEnrichment.printRes(res3)
          k -> res3.flatMap( x => x._2.map( y => (x._1 + y.fragList.mkString("_"),y.mean)) )
        case (k, _) => println(k," => only 1 value") ;  k ->Map()
      }
    }
}