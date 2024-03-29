package fr.inrae.p2m2.tools

case object ComputeCarbonMeanEnrichment {

  case class WorkObject(
                        code: String,
                        mean: Double,
                        fragList: Seq[String],
                        experimental: Boolean,
                        predecessor: Seq[String] = Seq()) {
    def hashcode: String = code+"_"+fragList.mkString("_")
    def isEqual(hashcodeComp: String) : Boolean = hashcode == hashcodeComp
  }

  def buildPlan(minC: Int, maxC: Int): Seq[(String, Seq[String])]  = {
    minC.to(maxC).flatMap(
      nc1 =>
        maxC.to(minC, -1).flatMap(
          nc2 => CarbonArrangement.planningComputedAdditionalValues(s"C${nc1}C${nc2}")
        ))
  }

  def setMeanAndFragment(meanEnrichment: Map[String, Seq[(Double, Seq[String])]])
  : (Seq[(String, Seq[String])], Map[String, Seq[WorkObject]]) = {
    val maxC = meanEnrichment
      .keys
      .flatMap(CarbonArrangement.code2Indexes)
      .maxBy(_._2)._2

    val minC = meanEnrichment
      .keys
      .flatMap(CarbonArrangement.code2Indexes)
      .minBy(_._1)._1

    val longestCodeCarbon: String = s"C${minC}C${maxC}"
    val plan: Seq[(String, Seq[String])] = buildPlan(minC,maxC)

    (plan, (plan.flatMap(_._2).distinct :+ longestCodeCarbon).distinct.map(
      (code: String) => code -> meanEnrichment.getOrElse(code, Seq())
        .map {
          case x if x._2 == Seq() => WorkObject(code, x._1, Seq("*EXP*"),experimental = true)
          case x => WorkObject(code, x._1, x._2,experimental = true)
        }
    ).toMap)
  }


  def eval(meanEnrichment: Map[String, Seq[WorkObject]], plan: Seq[(String, Seq[String])])
  : Map[String, Seq[WorkObject]] = {

    computeValues(meanEnrichment, plan)
  }

  def computeValuesRecursive(meanEnrichment: Map[String, Seq[WorkObject]],
                             executionPlan: Seq[(String, Seq[String])]): Map[String, Seq[WorkObject]] = {

    val res = computeValues(meanEnrichment, executionPlan)

    //printRes(res)

    res match {
      case m if meanEnrichment != m => computeValuesRecursive(m, executionPlan)
      case m => m
    }
  }

  /*
      Get all possibilities values/fragment sum
   */
  def listSumValuesPossibilities(m: Map[String, Seq[WorkObject]]): Seq[Seq[(String, WorkObject)]] = {
    m.headOption match {
      case Some((code: String, l: Seq[WorkObject])) =>
        listSumValuesPossibilities(m.drop(1)).flatMap(listSol => l.map(x => (x.code, x) +: listSol))
      case None => Seq(Seq())
    }
  }

  def computeValues(
                     meanEnrichment: Map[String, Seq[WorkObject]],
                     executionPlan: Seq[(String, Seq[String])]
                   ): Map[String, Seq[WorkObject]] = {

    meanEnrichment.map {
      case (code, meanEnrichAndWeight) => {
        val R1: (String, Seq[WorkObject]) = code -> (
          executionPlan
            .filter(x => x._1 == code)
            .filter(_._2.size > 1)
            .flatMap {
              case listCodeAdd: (String, Seq[String]) =>
          //      println(s"\n\n============= LEFT SEARCH ${code} ==========")
          //      println(s"PLAN EXEC : LEFT(${listCodeAdd._1})  RIGHT(${listCodeAdd._2.mkString(",")})")
                /**
                 * method to compute C1C3
                 * CIC3 = 1) 2*C1C2 + C3  or 2) C1 + 2*C2C3
                 * */
                val rightValuesMap: Map[String, Seq[WorkObject]] =
                  meanEnrichment
                    .filter(x => listCodeAdd._2.contains(x._1)) //

            //    println(rightValuesMap)
            //    println(listSumValuesPossibilities(rightValuesMap))
                listSumValuesPossibilities(rightValuesMap).flatMap {

                  //                          code,  (mean,  frag)
                  case listValuesPoss: Seq[(String, WorkObject)] =>
                    val successors: Seq[String] = listValuesPoss.map(_._2.code)
                    val predecessor: Seq[String] = listValuesPoss.flatMap(_._2.predecessor).distinct
/*
                  println("Sum des valeurs=>",listValuesPoss.map(_._1).mkString("+")+" ou "+listValuesPoss.map(_._2.code).mkString("+"))
                  println("succ****************", successors)
                  println("pred****************", predecessor)
                  println(successors intersect predecessor)
*/
                    // if intersection non empty => data is linked => no computation
                    if ((successors intersect predecessor).isEmpty) {
  //                    println(s"CRITERE K => INTERSECTION VIDE")
    //                  println(listValuesPoss)
                      val sumValues: Seq[(Double, Double)] = listValuesPoss.map(x => (x._2.mean, CarbonArrangement.weight(x._1)))

                      val meanEnrichmentComputed =
                        CarbonArrangement.sumMeanEnrichment(sumValues, CarbonArrangement.weight(code))

                      val fragmentComputed: Seq[String] = listValuesPoss.flatMap(x => x._2.fragList).distinct.sorted

                      Some(WorkObject(code, meanEnrichmentComputed,
                        fragmentComputed, experimental = false,predecessor = (successors ++ predecessor)))
                    } else {
                      None
                    }
                }
            } ++ (executionPlan
            .filter(_._2.size > 1)
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
                val leftCode: String = listCodeAdd._1
                val rightValues = listCodeAdd._2.filter(x => x != code)
                val rightValuesMap: Map[String, Seq[WorkObject]] = meanEnrichment.filter(x => rightValues.contains(x._1))

                //println("right values=>",rightValuesMap.mkString(","))
                listSumValuesPossibilities(rightValuesMap).flatMap {
                  //                          code,  mean,  frag
                  case listValuesPoss: Seq[(String, WorkObject)] => {
                    //  println("POSSIBILITY:::",listValuesPoss.mkString(","))
                    val sumValues: Seq[(Double, Double)] = listValuesPoss.map(x => (x._2.mean, CarbonArrangement.weight(x._1)))

                    meanEnrichment.getOrElse(leftCode,Seq()).flatMap(
                      v => {
                        val successors: Seq[String] = listValuesPoss.map(_._2.code) :+ v.code
                        val predecessor: Seq[String] = (listValuesPoss.flatMap(_._2.predecessor) ++ v.predecessor).distinct

                        // if intersection non empty => data is linked => no computation
                        if ((successors intersect predecessor).isEmpty) {
                          //  println(s"CRITERE K => INTERSECTION VIDE")

                          val isoVal: (Double, Double) = (v.mean, CarbonArrangement.weight(leftCode))

                          val fragmentComputed: Seq[String] = (listValuesPoss.flatMap(x => x._2.fragList) ++ v.fragList).distinct.sorted

                          val meanEnrichmentComputed =
                            CarbonArrangement.diffMeanEnrichment(isoVal, sumValues, CarbonArrangement.weight(code))
                          //  println(WorkObject(code,meanEnrichmentComputed, fragmentComputed, predecessor = (successors++predecessor)))
                          Some(WorkObject(
                            code,
                            meanEnrichmentComputed,
                            fragmentComputed,
                            experimental = false,
                            predecessor = (successors ++ predecessor)))
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

  def get(arrangement : String, meanEnrichment: Map[String, Seq[WorkObject]]) : Seq[WorkObject] = {

    // if exist get selected WorkObject
    meanEnrichment.filter(_._1 == arrangement).flatMap(_._2).toSeq
  }
  /*
    build all product possibility an array of several array
    Example :
    [[a,b]  [c,d]] -> [ [a,c] [a,d] [b,c] [b,d]]
   */
  def sumPossibilities[A](arr : Seq[Seq[A]]) : Seq[Seq[A]] = {
      arr match {
        case Seq(a) => a.map(Seq(_))
        case Seq(array : Seq[A],_*) =>
          array.flatMap( valueA => sumPossibilities(arr.drop(1)).map( arr2 => valueA +: arr2 ))
        case Seq() => Seq()
      }
  }

  def computeSingleValue(
                        arrangement : String,
                        executionPlan: Seq[(String, Seq[String])],
                        meanEnrichment: Map[String, Seq[WorkObject]],
                   ): Seq[WorkObject] = {

    /* Arrangement should be on the right or left side, otherwise it is not possible to compute value */
    val leftSide : Seq[Seq[String]] = executionPlan.filter(_._1 == arrangement).map(_._2)
    val rghtSide : Seq[(String, Seq[String])] = executionPlan.filter( _._2.contains(arrangement) )

    ( leftSide, rghtSide ) match {
      case (s1,_) if s1.nonEmpty =>
        s1.map(
          listRightTerm =>
            listRightTerm.map(term => get(term, meanEnrichment))
        )
          // remove all possibility with missing WO (sum of right terms is not computable)
          .filter(!_.contains(Seq()))
          // flatting all possibility to compute sum of WO
          .flatMap(sumPossibilities)
         .map {
            case arrayWoToSum : Seq[WorkObject] =>
              val sumValues: Seq[(Double, Double)] = arrayWoToSum.map(x => (x.mean, CarbonArrangement.weight(x.code)))

              val meanEnrichmentComputed =
                CarbonArrangement.sumMeanEnrichment(sumValues, CarbonArrangement.weight(arrangement))

              val fragmentComputed: Seq[String] = arrayWoToSum.flatMap(x => x.fragList).distinct.sorted

              WorkObject(arrangement, meanEnrichmentComputed,
                fragmentComputed, experimental = false,
                predecessor = arrayWoToSum.map(_.hashcode)
              )
          }

      case (_,s2) if s2.nonEmpty =>
        s2.map{
          case (leftTerm : String, rightTerms : Seq[String]) =>
            (
              get(leftTerm,meanEnrichment),
              rightTerms.filter(_ != arrangement).map(term => get(term, meanEnrichment)))
        }.filter {
          // remove all possibility with missing WO
          case (leftTerm : Seq[WorkObject], rightTerms : Seq[Seq[WorkObject]]) =>
            leftTerm.nonEmpty && (! rightTerms.contains(Seq()))
        }.map {
              /*
                transform the duple into a single array of Seq[Seq[WO]]
                the first element is the LeftTerm, from the second, it's all right terms
              */
          case (leftTerm : Seq[WorkObject], rightTerms : Seq[Seq[WorkObject]]) =>
            leftTerm +: rightTerms
        }
          .flatMap(sumPossibilities)
          .map {
            case arrayToDissect: Seq[WorkObject] =>
              val leftTerm = arrayToDissect.head
              val rightTerms = arrayToDissect.drop(1)

              val isoVal: (Double, Double) = (leftTerm.mean, CarbonArrangement.weight(leftTerm.code))

              val fragmentComputed: Seq[String] = (rightTerms.flatMap(x => x.fragList) ++ leftTerm.fragList).distinct.sorted
              val sumValues: Seq[(Double, Double)] = rightTerms.map(x => (x.mean, CarbonArrangement.weight(x.code)))

              val meanEnrichmentComputed =
                CarbonArrangement.diffMeanEnrichment(isoVal, sumValues, CarbonArrangement.weight(arrangement))

              WorkObject(
                arrangement,
                meanEnrichmentComputed,
                fragmentComputed,
                experimental = false,
                predecessor = (leftTerm+:rightTerms).map(_.hashcode))
          }
      case (_,_) =>
        System.err.println(s"Can not find $arrangement in the execution plan: $executionPlan")
        Seq()
    }

  }

  def printRes(res: Map[String, Seq[WorkObject]], valueDisplay: Boolean = true): Unit = {
    println(" =============== MAP ==============================================")
    for (elem <- res) {
      println(s"   === ${elem._1} === bs nb values:${elem._2.size}")
      if (valueDisplay) {
        elem._2.foreach {
          case el => println(s"\tVALUE:${el.mean}\tFRAG:${el.fragList.mkString(",")}" +
            s"\t\t[${el.predecessor.mkString(",")}]")
        }
      }
    }
  }

}
