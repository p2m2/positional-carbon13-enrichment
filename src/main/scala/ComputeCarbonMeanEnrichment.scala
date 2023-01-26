package fr.inrae.p2m2.tools

case object ComputeCarbonMeanEnrichment {

  def eval(meanEnrichment: Map[String, Seq[(Double, String)]]): Map[String, Seq[(Double, String)]] = {

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
      (x:String) => x -> meanEnrichment.getOrElse(x,Seq())
    ).toMap

    computeValuesRecursive(meanEnrichmentWithUnknownValues,plan, longestCodeCarbon)
  }

  def computeValuesRecursive(meanEnrichment: Map[String, Seq[(Double, String)]],
                             executionPlan: Seq[Seq[String]],
                             longestCodeCarbon: String): Map[String, Seq[(Double, String)]] = {

    println("start")
    println(meanEnrichment)
    val res = computeValues(meanEnrichment,executionPlan,longestCodeCarbon)

    println("res",res)
    res match {
        case m if meanEnrichment != m  => println("111111"); computeValuesRecursive( m,executionPlan, longestCodeCarbon)
        case m => println("2222222");println(meanEnrichment);println(m);m
    }
  }

  /*
      Get all possibilities values/fragment sum
   */
  def listSumValuesPossibilities(m: Map[String, Seq[(Double, String)]]): Seq[Seq[(String,Double, String)]] = {
    m.headOption match {
      case Some((code: String, l: Seq[(Double, String)])) =>
   listSumValuesPossibilities(m.drop(1)).flatMap( listSol => l.map( x => (code,x._1,x._2) +: listSol ))
      case None => Seq(Seq())
    }
  }

  def computeValues(
                     meanEnrichment: Map[String, Seq[(Double, String)]],
                     executionPlan: Seq[Seq[String]],
                     longestCodeCarbon: String
                   ): Map[String, Seq[(Double, String)]] = {
    println("==============================================RRRRRRRRRRRRRRRRRRR===================")
    meanEnrichment.map {
      case (code, meanEnrichAndWeight) =>
        code -> (executionPlan.flatMap {
          case listCodeAdd =>
            /**
             * C1C3 -> [C1C2, C3] ou [C1, C2C3]
             * */
            if (listCodeAdd.contains(code)) {
             // println("1****************************",code)
              /**
               * method to compute C1C2 or C3 or C1
               * example C1 = 3*C1C3 - 2*C2C3
               * */
              val rightValues = listCodeAdd.filter(x => x != code)
              println(rightValues,"pour calcul=>",code)
              val rightValuesMap: Map[String, Seq[(Double, String)]] = meanEnrichment.filter(x => rightValues.contains(x._1))
              listSumValuesPossibilities(rightValuesMap).flatMap{
                //                          code,  mean,  frag
                  case listValuesPoss : Seq[(String,Double,String)] => {
                    val sumValues: Seq[(Double, Double)] = listValuesPoss.map(x => (x._2, CarbonArrangement.weight(x._1)) )

                    meanEnrichment(longestCodeCarbon).map(
                      v => {
                        val isoVal: (Double, Double) = (v._1, CarbonArrangement.weight(longestCodeCarbon))

                        val fragmentComputed: String = (listValuesPoss.map(_._3) :+ v._2).distinct.filter(_!="").mkString("_")

                        val meanEnrichmentComputed =
                          CarbonArrangement.diffMeanEnrichment(isoVal, sumValues, CarbonArrangement.weight(longestCodeCarbon))

                        println(meanEnrichmentComputed, fragmentComputed)
                        (meanEnrichmentComputed, fragmentComputed)
                      })
                  }
                }

            } else if (code == longestCodeCarbon) {
              //println("2****************************",longestCodeCarbon)

              /**
               * method to compute C1C3
               * CIC3 = 1) 2*C1C2 + C3  or 2) C1 + 2*C2C3
               * */
              val rightValuesMap: Map[String, Seq[(Double, String)]] = meanEnrichment.filter(x => listCodeAdd.contains(x._1))
              listSumValuesPossibilities(rightValuesMap).map {
                //                          code,  mean,  frag
                case listValuesPoss: Seq[(String, Double, String)] =>
                  val sumValues: Seq[(Double, Double)] = listValuesPoss.map(x => (x._2, CarbonArrangement.weight(x._1)))

                  val meanEnrichmentComputed =
                    CarbonArrangement.sumMeanEnrichment(sumValues, CarbonArrangement.weight(longestCodeCarbon))

                  val fragmentComputed: String = listValuesPoss.map(_._3).distinct.filter(_ != "").mkString("_")
                  println(meanEnrichmentComputed, fragmentComputed)
                  (meanEnrichmentComputed, fragmentComputed)
              }
            } else {
              Seq()
            }
        }++ meanEnrichAndWeight).distinct //
    }
  }
}
