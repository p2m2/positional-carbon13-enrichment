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

          // get the biggest Carbon to build in silico possibility
          val maxC = mapArrangementCarbon13
            .keys
            .flatMap(CarbonArrangement.code2Indexes)
            .maxBy(_._2)._2

          val minC = mapArrangementCarbon13
            .keys
            .flatMap(CarbonArrangement.code2Indexes)
            .minBy(_._1)._1

          val longestCodeCarbon = s"C${minC}C${maxC}"
          val plan = CarbonArrangement.planningComputedAdditionalValues(longestCodeCarbon)

          println(s"minC=$minC maxC=$maxC codeCarbon=$longestCodeCarbon")
          println(plan)

          /* Compute new values */
          val workWithAllValues: Map[String, Seq[(Double, Seq[String])]] =
            mapArrangementCarbon13

          println(workWithAllValues)
          val (l, p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(workWithAllValues)
          //pour les test deux appels
          val res = ComputeCarbonMeanEnrichment.computeValues(r, p, l)
          ComputeCarbonMeanEnrichment.printRes(res)
          k -> res.flatMap( x => x._2.map( y => (x._1 + y.fragList.mkString("_"),y.mean)) )
        case (k, _) => println(k," => only 1 value") ;  k ->Map()
      }
    }
}