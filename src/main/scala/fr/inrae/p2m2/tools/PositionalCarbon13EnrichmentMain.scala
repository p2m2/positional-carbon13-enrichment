package fr.inrae.p2m2.tools

import org.scalajs.dom
import org.scalajs.dom.{Blob}

object PositionalCarbon13EnrichmentMain {
  def main(args: Array[String]): Unit = {

      println("ok")
      val reader = new dom.FileReader()
      reader.readAsText(args(0).asInstanceOf[Blob])
      reader.onload = (_) => {
        val contents = reader.result.asInstanceOf[String]

        val listMeanEnrichment = IsocorReader.getMeanEnrichmentByFragment(contents)
        listMeanEnrichment
          .groupBy(x => (x.sample, x.derivative, x.metabolite))
          .take(3) // debugging.....
          .foreach {
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

              /* Compute new values */
              val workWithAllValues: Map[String, Seq[(Double, Seq[String])]] =
                mapArrangementCarbon13

              println(workWithAllValues)
              val (l, p, r) = ComputeCarbonMeanEnrichment.setMeanAndFragment(workWithAllValues)
              val res = ComputeCarbonMeanEnrichment.computeValues(r, p, l)

              println(mapArrangementCarbon13, minC, maxC)
              println(plan)
            case (k, _) => //println(k," => only 1 value")
          }


        dom.console.log(contents)
      }
  }
}
