package fr.inrae.p2m2.tools

import scala.collection.immutable.Seq

case object ComputeCarbonMeanEnrichment {

  case class WorkObject(mean : Double, fragList : Seq[String],computeWith:Seq[(String,String)]=Seq()) {
    def isComputed(d:Seq[(String,String)]) :  Boolean = d.exists(x => !computeWith.contains(x))
  }

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
      (x:String) => x -> meanEnrichment.getOrElse(x,Seq())
        .map{case x if x._2==Seq() => WorkObject(x._1,Seq("*EXP*")) case x => WorkObject(x._1,x._2) }
    ).toMap
    println("=====================    EVAL ===================================")
    computeValues(meanEnrichmentWithUnknownValues,plan, longestCodeCarbon)
      .map{
        case (k,v) => k->(v.map( x => (x.mean,x.fragList)))
      }
  }

  def computeValuesRecursive(meanEnrichment: Map[String, Seq[WorkObject]],
                             executionPlan: Seq[Seq[String]],
                             longestCodeCarbon: String): Map[String, Seq[WorkObject]] = {

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
  def listSumValuesPossibilities(m: Map[String, Seq[WorkObject]]): Seq[Seq[(String,WorkObject)]] = {
    m.headOption match {
      case Some((code: String, l: Seq[WorkObject])) =>
   listSumValuesPossibilities(m.drop(1)).flatMap( listSol => l.map( x => (code,x) +: listSol ))
      case None => Seq(Seq())
    }
  }

  def computeValues(
                     meanEnrichment: Map[String, Seq[WorkObject]],
                     executionPlan: Seq[Seq[String]],
                     longestCodeCarbon: String
                   ): Map[String, Seq[WorkObject]] = {
    meanEnrichment.map {
      case (code, meanEnrichAndWeight) if (code == longestCodeCarbon) =>
        code -> (executionPlan.flatMap {
          case listCodeAdd =>
        /**
         * method to compute C1C3
         * CIC3 = 1) 2*C1C2 + C3  or 2) C1 + 2*C2C3
         * */
        val rightValuesMap: Map[String, Seq[WorkObject]] =
          meanEnrichment.filter(x => listCodeAdd.contains(x._1))

            listSumValuesPossibilities(rightValuesMap).flatMap {
          //                          code,  mean,  frag
              case listValuesPoss: Seq[(String, WorkObject)] =>
                val sumValues: Seq[(Double, Double)] = listValuesPoss.map(x => (x._2.mean, CarbonArrangement.weight(x._1)))

                val meanEnrichmentComputed =
                  CarbonArrangement.sumMeanEnrichment(sumValues, CarbonArrangement.weight(longestCodeCarbon))

                val fragmentComputed: Seq[String] = listValuesPoss.flatMap(x =>x._2.fragList).distinct.sorted
                val fragmentComputedToCompare: Seq[String] = listValuesPoss.flatMap(x =>{x._2.fragList.map(y => x._1+"_"+y)}).distinct.sorted

                if ( ! meanEnrichAndWeight.map(_.fragList).contains(fragmentComputedToCompare) ) {
                  Some(WorkObject(meanEnrichmentComputed, fragmentComputed))
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
            val rightValuesMap: Map[String, Seq[WorkObject]] = meanEnrichment.filter(x => rightValues.contains(x._1))
            listSumValuesPossibilities(rightValuesMap).flatMap{
              //                          code,  mean,  frag
                case listValuesPoss : Seq[(String,WorkObject)] => {
                  val sumValues: Seq[(Double, Double)] = listValuesPoss.map(x => (x._2.mean, CarbonArrangement.weight(x._1)) )

                  meanEnrichment(longestCodeCarbon).flatMap(
                    v => {
                      val isoVal: (Double, Double) = (v.mean, CarbonArrangement.weight(longestCodeCarbon))

                      val fragmentComputedToCompare: Seq[String] = (listValuesPoss.flatMap(x => { println(x._1);x._2.fragList.map(y => x._1+"_"+y) })
                        ++ v.fragList.map(x => longestCodeCarbon+"_"+x)).distinct.sorted
                      val fragmentComputed: Seq[String] = (listValuesPoss.flatMap(x => x._2.fragList) ++ v.fragList).distinct.sorted
                    //  meanEnrichment(longestCodeCarbon).map(_._2).contains( fragmentComputed )
                      // if fragment exist do not !!!
                      println("CURRENT FRAGMENT",fragmentComputed)
                      println("CURRENT FRAGMENT COMP",fragmentComputedToCompare)
                      println("meanEnrichment",meanEnrichment(longestCodeCarbon).map(_.fragList))
                      if (  ! meanEnrichment(longestCodeCarbon).map(_.fragList).contains( fragmentComputedToCompare ) ) {
                         val meanEnrichmentComputed =
                          CarbonArrangement.diffMeanEnrichment(isoVal, sumValues, CarbonArrangement.weight(longestCodeCarbon))

                        Some(WorkObject(meanEnrichmentComputed, fragmentComputed))
                      } else {
                        None
                      }

                    })
                }
              }
        } ++ meanEnrichAndWeight).distinct //
    }
  }

  def printRes(res:Map[String, Seq[WorkObject]]) : Unit = {
      println("\n\n ==== STEP =====\n\n")
      for (elem <- res) {
        println(elem._1)
        elem._2.foreach {
          case el => println(el.mean, el.fragList)
        }
      }
    println("\n")
  }

  def printRes2(res: Map[String, Seq[(Double,Seq[String])]]): Unit = {
    println("\n\n ==== STEP =====\n\n")
    for (elem <- res) {
      println(elem._1)
      elem._2.foreach {
        case el => println(el._1, el._2.mkString(":"))
      }
    }
    println("\n")
  }
}
