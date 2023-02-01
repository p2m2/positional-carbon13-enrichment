package fr.inrae.p2m2.tools


case object ComputeCarbonMeanEnrichment {

  case class WorkObject(code : String,mean : Double, fragList : Seq[String], predecessor:Seq[String]=Seq()) {
    def hashcode : String = code
  }

  def getMeanAndFragment(l : Map[String,Seq[WorkObject]]) : Map[String, Seq[(Double, Seq[String])]] =
    l.map {
        case (k,v) => k -> v.map( y => (y.mean,y.fragList))
    }

  def setMeanAndFragment(meanEnrichment: Map[String, Seq[(Double, Seq[String])]])
  : (String,Seq[(String,Seq[String])],Map[String,Seq[WorkObject]]) = {
    val maxC = meanEnrichment
      .keys
      .flatMap(CarbonArrangement.code2Indexes)
      .maxBy(_._2)._2

    val minC = meanEnrichment
      .keys
      .flatMap(CarbonArrangement.code2Indexes)
      .minBy(_._1)._1

    val longestCodeCarbon : String = s"C${minC}C${maxC}"
  //  val plan: Seq[(String,Seq[String])] = CarbonArrangement.planningComputedAdditionalValues(longestCodeCarbon)
    val plan: Seq[(String,Seq[String])] =
    minC.to(maxC).flatMap(
      nc1 =>
        maxC.to(minC,-1).flatMap(
          nc2 =>  CarbonArrangement.planningComputedAdditionalValues(s"C${nc1}C${nc2}")
    ))

    (longestCodeCarbon,plan,(plan.flatMap(_._2).distinct :+ longestCodeCarbon).distinct.map(
      (code: String) => code -> meanEnrichment.getOrElse(code, Seq())
        .map {
          case x if x._2 == Seq() => WorkObject(code,x._1, Seq("*EXP*"))
          case x => WorkObject(code,x._1, x._2)
        }
    ).toMap)
  }


  def eval(meanEnrichment: Map[String,Seq[WorkObject]],longestCodeCarbon: String, plan:Seq[(String,Seq[String])] )
  : Map[String,Seq[WorkObject]] = {

    computeValues(meanEnrichment,plan, longestCodeCarbon)
  }

  def computeValuesRecursive(meanEnrichment: Map[String, Seq[WorkObject]],
                             executionPlan: Seq[(String,Seq[String])],
                             longestCodeCarbon: String): Map[String, Seq[WorkObject]] = {

    val res = computeValues(meanEnrichment,executionPlan,longestCodeCarbon)

    //printRes(res)

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
        listSumValuesPossibilities(m.drop(1)).flatMap( listSol => l.map( x => (x.code,x) +: listSol ))
      case None => Seq(Seq())
    }
  }

  def computeValues(
                     meanEnrichment: Map[String, Seq[WorkObject]],
                     executionPlan: Seq[(String,Seq[String])],
                     longestCodeCarbon: String
                   ): Map[String, Seq[WorkObject]] = {

    meanEnrichment.map {
      case (code, meanEnrichAndWeight) =>{
        val R1 : (String,Seq[WorkObject]) = code -> (
          executionPlan
            .filter(x => x._1 == code)
            .filter(_._2.size>1)
            .flatMap {
            case listCodeAdd: (String, Seq[String]) =>
             // println(s"\n\n============= LEFT SEARCH ${code} ==========")
             // println(s"PLAN EXEC : LEFT(${listCodeAdd._1})  RIGHT(${listCodeAdd._2.mkString(",")})")
              /**
               * method to compute C1C3
               * CIC3 = 1) 2*C1C2 + C3  or 2) C1 + 2*C2C3
               * */
              val rightValuesMap: Map[String, Seq[WorkObject]] =
                meanEnrichment
                  .filter(x => listCodeAdd._2.contains(x._1)) //

             // println(rightValuesMap)
             // println(listSumValuesPossibilities(rightValuesMap))
              listSumValuesPossibilities(rightValuesMap).flatMap {

                //                          code,  (mean,  frag)
                case listValuesPoss: Seq[(String, WorkObject)] =>
                  val successors: Seq[String] = listValuesPoss.map(_._2.hashcode)
                  val predecessor: Seq[String] = listValuesPoss.flatMap(_._2.predecessor).distinct
/*
                  println("Sum des valeurs=>",listValuesPoss.map(_._1).mkString("+")+" ou "+listValuesPoss.map(_._2.code).mkString("+"))
                  println("succ****************", successors)
                  println("pred****************", predecessor)
                  println(successors intersect predecessor)
  */                // if intersection non empty => data is linked => no computation
                  if ((successors intersect predecessor).isEmpty) {
                   // println(s"CRITERE K => INTERSECTION VIDE")
                   // println(listValuesPoss)
                    val sumValues: Seq[(Double, Double)] = listValuesPoss.map(x => (x._2.mean, CarbonArrangement.weight(x._1)))

                    val meanEnrichmentComputed =
                      CarbonArrangement.sumMeanEnrichment(sumValues, CarbonArrangement.weight(code))

                    val fragmentComputed: Seq[String] = listValuesPoss.flatMap(x => x._2.fragList).distinct.sorted

                    Some(WorkObject(code,meanEnrichmentComputed,
                      fragmentComputed, predecessor = (successors ++ predecessor)))
                  } else {
                    None
                  }
              }
        } ++ (executionPlan
            .filter(_._2.size>1)
            .filter(_._2.contains(code))
          .flatMap {
            case listCodeAdd =>
            //  println(s"\n\n============= RIGHT SEARCH ${code} ==========")
            //  println(s"PLAN EXEC : LEFT(${listCodeAdd._1})  RIGHT(${listCodeAdd._2.mkString(",")})")

              /**
               * C1C3 -> [C1C2, C3] ou [C1, C2C3]
               * */

              /**
               * method to compute C1C2 or C3 or C1
               * example C1 = 3*C1C3 - 2*C2C3
               * */
              val leftCode : String = listCodeAdd._1
              val rightValues = listCodeAdd._2.filter(x => x != code)
              val rightValuesMap: Map[String, Seq[WorkObject]] = meanEnrichment.filter(x => rightValues.contains(x._1))

              //println("right values=>",rightValuesMap.mkString(","))
              listSumValuesPossibilities(rightValuesMap).flatMap {
                //                          code,  mean,  frag
                case listValuesPoss: Seq[(String, WorkObject)] => {
                //  println("POSSIBILITY:::",listValuesPoss.mkString(","))
                  val sumValues: Seq[(Double, Double)] = listValuesPoss.map(x => (x._2.mean, CarbonArrangement.weight(x._1)))

                  meanEnrichment(leftCode).flatMap(
                    v => {
                      val successors: Seq[String] = listValuesPoss.map(_._2.hashcode) :+ v.hashcode
                      val predecessor: Seq[String] = (listValuesPoss.flatMap(_._2.predecessor) ++ v.predecessor).distinct
/*
                      println("2222")
                      println("==>",listValuesPoss.mkString(",") +" ----- "+v)
                      println("Sum des valeurs=>",successors.mkString("+/-"))
                      println("succ****************", successors)
                      println("pred****************", predecessor)
                      println(successors intersect predecessor)
                      println(successors intersect predecessor)*/
                      // if intersection non empty => data is linked => no computation
                      if ((successors intersect predecessor).isEmpty) {
                    //  println(s"CRITERE K => INTERSECTION VIDE")

                        val isoVal: (Double, Double) = (v.mean, CarbonArrangement.weight(leftCode))

                        val fragmentComputed: Seq[String] = (listValuesPoss.flatMap(x => x._2.fragList) ++ v.fragList).distinct.sorted

                        val meanEnrichmentComputed =
                          CarbonArrangement.diffMeanEnrichment(isoVal, sumValues, CarbonArrangement.weight(code))
                      //  println(WorkObject(code,meanEnrichmentComputed, fragmentComputed, predecessor = (successors++predecessor)))
                        Some(WorkObject(code,meanEnrichmentComputed, fragmentComputed, predecessor = (successors++predecessor)))
                      } else {
                        None
                      }
                    })
                }
              }
          }) ++ meanEnrichAndWeight).distinct
        R1
      }
    }
  }

  def printRes(res:Map[String, Seq[WorkObject]],valueDisplay : Boolean = true) : Unit = {
    println(" =============== MAP ==============================================")
    for (elem <- res) {
        println(s"   === ${elem._1} === bs nb values:${elem._2.size}")
      if (valueDisplay) {
        elem._2.foreach {
          case el => println(s"\tVALUE:${el.mean}\tFRAG:${el.fragList.mkString(",")}"+
            s"\t\t[${el.predecessor.mkString(",")}]")
        }
      }
    }
  }

}
