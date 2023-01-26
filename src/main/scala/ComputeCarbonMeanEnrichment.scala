package fr.inrae.p2m2.tools

case object ComputeCarbonMeanEnrichment {
  def computeValues(
                     meanEnrichment: Map[String, Option[(Double, String)]],
                     executionPlan: Seq[Seq[String]],
                     longestCodeCarbon: String
                   ): Map[String, Option[(Double, String)]] = {
    meanEnrichment.flatMap {
      case (code, meanEnrichAndWeight) if meanEnrichAndWeight.isDefined => Seq( (code -> meanEnrichAndWeight) )
      case (code, meanEnrichAndWeight) if meanEnrichAndWeight.isEmpty =>
        executionPlan.flatMap {
          case listCodeAdd =>
            println("1****************************")

            /**
             * C1C3 -> [C1C2, C3] ou [C1, C2C3]
             * */
            if (listCodeAdd.contains(code)) {
              /**
               * method to compute C1C2 or C3 or C1
               * example C1 = 3*C1C3 - 2*C2C3
               * */
              val rightValues = listCodeAdd
                .filter(x => x != code)

              val sumValues: Seq[(Double, Double)] = rightValues.flatMap(x => meanEnrichment.getOrElse(x, None) match {
                case None => None
                case Some((v: Double, _)) => Some(v, CarbonArrangement.weight(x))
              })

              meanEnrichment(longestCodeCarbon).map(_._1) match {
                case Some(v) if sumValues.size == rightValues.size =>
                  println(s"?CODE:$code     PLAN:$rightValues")
                  val isoVal: (Double, Double) = (v, CarbonArrangement.weight(longestCodeCarbon))

                  val meanEnrichmentComputed =
                    CarbonArrangement.diffMeanEnrichment(isoVal, sumValues, CarbonArrangement.weight(longestCodeCarbon))

                  val fragmentComputed : String = (rightValues
                    .flatMap(x => meanEnrichment.getOrElse(x, None))
                    .map(_._2)
                    :+ meanEnrichment(longestCodeCarbon).map(_._2).getOrElse("")
                    ).distinct.filter(_!="").mkString("_")

                  Some( code -> Some(meanEnrichmentComputed,fragmentComputed) )

                case _ => None
              }

            } else if (code == longestCodeCarbon) {
              println("2****************************")

              /**
               * method to compute C1C3
               * CIC3 = 1) 2*C1C2 + C3  or 2) C1 + 2*C2C3
               * */
              val sumValues: Seq[(Double, Double)] = listCodeAdd.flatMap(x => meanEnrichment.getOrElse(x, None) match {
                case None => None
                case Some((v: Double, _)) => Some(v, CarbonArrangement.weight(x))
              })

              if (sumValues.size == listCodeAdd.size) {
                val meanEnrichmentComputed =
                  CarbonArrangement.sumMeanEnrichment(sumValues, CarbonArrangement.weight(longestCodeCarbon))

                val fragmentComputed: String = listCodeAdd
                  .flatMap(x => meanEnrichment.getOrElse(x, None))
                  .map(_._2)
                  .distinct.mkString("_")

                Some( code -> Some(meanEnrichmentComputed,fragmentComputed) )
              } else {
                None
              }
            } else {
              None
            }
          //diff
          // CarbonArrangement.d
        }
    }
  }
}
