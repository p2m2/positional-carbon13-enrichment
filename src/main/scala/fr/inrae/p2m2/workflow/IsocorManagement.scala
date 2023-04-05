package fr.inrae.p2m2.workflow

import fr.inrae.p2m2.tools._

case object IsocorManagement {

  def filterPlan(toCalculated :String, dependencies : Seq[String], execPlan : Seq[(String, Seq[String])])
  : Seq[(String, Seq[String])] = {
    execPlan.filter {
      case (leftTerm, rightTerms) if leftTerm == toCalculated =>
       // println(" 1 == leftTerm:", leftTerm, " righTerms:", rightTerms, " ask:", toCalculated)
        dependencies.sorted == rightTerms

      case (leftTerm, rightTerms) if rightTerms.contains(toCalculated) =>
       // println("2 == key:", toCalculated, " Plan (leftterm:", leftTerm, " righTerms:", rightTerms, ")   => dependences:", dependencies)
        dependencies.contains(leftTerm) &&
          (dependencies.filter(_ != leftTerm).sorted == rightTerms.filter(_ != toCalculated).sorted)

      case (_,_) => false
    }
  }

  def workflow(isocorContent : String, isotopeToCompute : Map[String,Seq[(String,Seq[String])]] = Map())
  : Map[SampleAndMetabolite, Seq[DataResults]] = {
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
              .map(isocorVal => isocorVal.code ->
                Seq((isocorVal.meanEnrichment, Seq(isocorVal.fragment))))
              .groupBy(_._1).map(x => x._1 -> x._2.flatMap(_._2))

          val (
            execPlan : Seq[(String, Seq[String])] ,
            meanEnrichmentValues : Map[String, Seq[ComputeCarbonMeanEnrichment.WorkObject]] ) =
            ComputeCarbonMeanEnrichment.setMeanAndFragment(mapArrangementCarbon13)

          val metabolite = k._2

          SampleAndMetabolite(k._1,k._2) -> {
            if(!isotopeToCompute.contains(metabolite)) {

              System.err.println(s"$metabolite have not desired isotope to compute. Try to search new isotope to infer")
              ComputeCarbonMeanEnrichment.computeValues(meanEnrichmentValues, execPlan)
            } else {
              val listIso : Seq[(String,Seq[String])] = isotopeToCompute(metabolite)

              listIso.foldLeft(meanEnrichmentValues){
                (acc,l) =>
                 // println(s"CAL FOR $metabolite / $l")
                  val np = filterPlan(l._1,l._2,execPlan)
                  //println(execPlan)
                  //println("NP:",np)
                  combineIterables(acc,Map( l._1->ComputeCarbonMeanEnrichment.computeSingleValue(l._1, np, acc)))
              }
            }
          }.flatMap(x => x._2
            .map {
              case y if y.fragList.exists(_.nonEmpty) =>
                DataResults(x._1 + "_" + y.fragList.filter(_.nonEmpty).mkString("_"), y.mean, y.experimental,y.predecessor)
              case y => DataResults(x._1, y.mean, y.experimental,y.predecessor)
            }).toSeq

        case (k, _) => println(k," => only 1 value") ;  SampleAndMetabolite(k._1,k._2) ->Seq()
      }
    }

  def combineIterables[K, V](a: Map[K, Seq[V]], b: Map[K, Seq[V]]): Map[K, Seq[V]] = {
    a ++ b.map { case (k, v) => k -> (v ++ a.getOrElse(k, Seq.empty)) }
  }
}