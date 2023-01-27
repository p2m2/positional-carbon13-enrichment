package fr.inrae.p2m2.tools

case object ComputeCarbonMeanEnrichment {

  def eval(meanEnrichment: Map[String, Seq[(Double, Seq[String])]]): Map[String, Seq[(Double, Seq[String])]] = {

    // get the biggest Carbon to build in silico possibility
    val maxC = meanEnrichment
      .keys
      .flatMap(CarbonArrangement.code2Indexes)
      .maxBy(_._2)._2

    val minC = meanEnrichment
      .keys
      .flatMap(CarbonArrangement.code2Indexes)
      .minBy(_._1)._1

    val longestCodeCarbon = s"C${minC}C${maxC}"
    val plan : Seq[Seq[String]] = CarbonArrangement.planningComputedAdditionalValues(longestCodeCarbon)

    val meanEnrichmentWithUnknownValues = ( plan.flatten :+ longestCodeCarbon ).distinct.map(
      (x:String) => x -> meanEnrichment.getOrElse(x,Seq()).map{case x if x._2==Seq() => (x._1,Seq("__EXP__")) case x => x }
    ).toMap

    computeValuesRecursive(meanEnrichmentWithUnknownValues,plan, longestCodeCarbon)
  }

  def computeValuesRecursive(meanEnrichment: Map[String, Seq[(Double, Seq[String])]],
                             executionPlan: Seq[Seq[String]],
                             longestCodeCarbon: String): Map[String, Seq[(Double, Seq[String])]] = {

    val res = computeValues(meanEnrichment,executionPlan,longestCodeCarbon)

    printRes(res)

    res match {
        case m if meanEnrichment != m  => computeValuesRecursive( m,executionPlan, longestCodeCarbon)
        case m => m
    }
  }

  /*
      Get all possibilities values/fragment sum
   */
  def listSumValuesPossibilities(m: Map[String, Seq[(Double, Seq[String])]]): Seq[Seq[(String,Double, Seq[String])]] = {
    m.headOption match {
      case Some((code: String, l: Seq[(Double, Seq[String])])) =>
   listSumValuesPossibilities(m.drop(1)).flatMap( listSol => l.map( x => (code,x._1,x._2) +: listSol ))
      case None => Seq(Seq())
    }
  }

  def computeValues(
                     meanEnrichment: Map[String, Seq[(Double, Seq[String])]],
                     executionPlan: Seq[Seq[String]],
                     longestCodeCarbon: String
                   ): Map[String, Seq[(Double, Seq[String])]] = {
    meanEnrichment.map {
      case (code, meanEnrichAndWeight) if (code == longestCodeCarbon) =>
        code -> (executionPlan.flatMap {
          case listCodeAdd =>
        /**
         * method to compute C1C3
         * CIC3 = 1) 2*C1C2 + C3  or 2) C1 + 2*C2C3
         * */
        val rightValuesMap: Map[String, Seq[(Double, Seq[String])]] =
          meanEnrichment.filter(x => listCodeAdd.contains(x._1))

            listSumValuesPossibilities(rightValuesMap).flatMap {
          //                          code,  mean,  frag
              case listValuesPoss: Seq[(String, Double, Seq[String])] =>
                val sumValues: Seq[(Double, Double)] = listValuesPoss.map(x => (x._2, CarbonArrangement.weight(x._1)))

                val meanEnrichmentComputed =
                  CarbonArrangement.sumMeanEnrichment(sumValues, CarbonArrangement.weight(longestCodeCarbon))

                val fragmentComputed: Seq[String] = listValuesPoss.flatMap(_._3).distinct.sorted
                if ( ! meanEnrichAndWeight.map(_._2).contains(fragmentComputed) ) {
                  Some(meanEnrichmentComputed, fragmentComputed)
                } else {
                  None
                }
            }
        } ++ meanEnrichAndWeight).distinct

      case (code, meanEnrichAndWeight) =>
        code -> (executionPlan
          .filter(listCodeAdd => listCodeAdd.contains(code))
          .flatMap {
          case listCodeAdd =>
            /**
             * C1C3 -> [C1C2, C3] ou [C1, C2C3]
             * */

            /**
             * method to compute C1C2 or C3 or C1
             * example C1 = 3*C1C3 - 2*C2C3
             * */
            val rightValues = listCodeAdd.filter(x => x != code)
            val rightValuesMap: Map[String, Seq[(Double, Seq[String])]] = meanEnrichment.filter(x => rightValues.contains(x._1))
            listSumValuesPossibilities(rightValuesMap).flatMap{
              //                          code,  mean,  frag
                case listValuesPoss : Seq[(String,Double,Seq[String])] => {
                  val sumValues: Seq[(Double, Double)] = listValuesPoss.map(x => (x._2, CarbonArrangement.weight(x._1)) )

                  meanEnrichment(longestCodeCarbon).flatMap(
                    v => {
                      val isoVal: (Double, Double) = (v._1, CarbonArrangement.weight(longestCodeCarbon))

                      val fragmentComputed: Seq[String] = (listValuesPoss.flatMap(_._3) ++ v._2).distinct.sorted
                    //  meanEnrichment(longestCodeCarbon).map(_._2).contains( fragmentComputed )
                      // if fragment exist do not !!!
                      if (  ! meanEnrichment(longestCodeCarbon).map(_._2).contains( fragmentComputed ) ) {
                        val meanEnrichmentComputed =
                          CarbonArrangement.diffMeanEnrichment(isoVal, sumValues, CarbonArrangement.weight(longestCodeCarbon))

                        Some(meanEnrichmentComputed, fragmentComputed)
                      } else {
                        None
                      }

                    })
                }
              }
        } ++ meanEnrichAndWeight).distinct //
    }
  }

  def printRes(res:Map[String, Seq[(Double, Seq[String])]]) : Unit = {
      println("\n\n ==== STEP =====\n\n")
      for (elem <- res) {
        println(elem._1)
        elem._2.foreach {
          case (value, listFrag) => println(value, listFrag)
        }
      }
    println("\n")
  }
}
