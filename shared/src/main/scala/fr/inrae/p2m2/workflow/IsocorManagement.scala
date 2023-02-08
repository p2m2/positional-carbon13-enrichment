package fr.inrae.p2m2.workflow

import fr.inrae.p2m2.tools._
case object IsocorManagement {

  def workflow(isocorContent : String)
  //     SAMPLE/METABOLITE    CODE_FRAG, MEAN_ENR, EXPERIMENTAL
  : Map[(String,String), Seq[(String,Double,Boolean)]] = {
    val listMeanEnrichment = IsocorReader.getMeanEnrichmentByFragment(isocorContent.replace("\r", "\n"))

    listMeanEnrichment
      .groupBy(x => (x.sample, x.metabolite))
   //   .take(3) // debugging.....
      .map {
        case (k, listValues: Seq[IsocorValue]) if listValues.distinct.size > 1 =>
          /* setting with experimental values     CX...CZ =>  value,FRAGMENT  ""*/
          val mapArrangementCarbon13: Map[String, Seq[(Double, Seq[String])]] =
            listValues
              .distinct
              .map {
                case isocorVal => isocorVal.code -> Seq((isocorVal.meanEnrichment, Seq(isocorVal.fragment)))
              }.groupBy(_._1).map( x => x._1 -> x._2.flatMap(_._2))

          val (p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(mapArrangementCarbon13)

          //val res = ComputeCarbonMeanEnrichment.computeValues(r, p)
          for (elem <- ComputeCarbonMeanEnrichment.computeValues(r, p)) {println(elem._2)}
          k -> ComputeCarbonMeanEnrichment.computeValues(r, p).flatMap( x => x._2
            .map{
              case y if y.fragList.exists(_.nonEmpty) => (x._1 + "_" + y.fragList.filter(_.nonEmpty).mkString("_"),y.mean,y.experimental)
              case y => (x._1,y.mean,y.experimental)
            }).toSeq
        case (k, _) => println(k," => only 1 value") ;  k ->Seq()
      }
    }
}