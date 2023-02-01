package fr.inrae.p2m2.tools

import java.io.File
import scala.io.Source

case object IsocorReader {

  def extractInformatiponMetaboliteName(metabolite: String): Option[(String, Int, Option[Int], String)] = {
    /* metabolite => [NAME]CxCy[FragmentName]*/
    val pattern1 = "(\\w+)C([0-9])C([0-9])(\\w*)".r
    val pattern2 = "(\\w+)C([0-9])C([0-9])".r
    val pattern3 = "(\\w+)C([0-9])(\\w*)".r


    metabolite match {
      case pattern1(metaboliteName, carbonStart, carbonEnd, fragmentName) =>
        Some(metaboliteName, carbonStart.toInt, Some(carbonEnd.toInt), fragmentName)
      case pattern2(metaboliteName, carbonStart, carbonEnd) =>
        Some(metaboliteName, carbonStart.toInt, Some(carbonEnd.toInt), "")
      case pattern3(metaboliteName, carbonStart, fragmentName) =>
        Some(metaboliteName, carbonStart.toInt, None, fragmentName)
      case _ => None
    }

  }

  /*
     f : Isocor file

     return Seq[IsocorValue]
   */
  def getMeanEnrichmentByFragment(content : String): Seq[IsocorValue] = {
    println("===========================")


    val lines= content.trim.split("\n")
    println(lines(0))
    val header: Map[String, Int] =
      lines(0)
        .split("[ ;\t]")
        .toSeq
        .zipWithIndex
        .map { case (title, idx) => title -> idx }
        .toMap

    if( Seq("sample", "metabolite", "derivative", "mean_enrichment").map(header.contains).exists(!_) )
      throw new IllegalArgumentException("Can not find header: sample metabolite derivative mean_enrichment")

    val listEntries: Seq[IsocorValue] =
      lines.slice(1,lines.length)
      .flatMap(
        f = line => {
          val res = line
            .split("[ ;\t]")
          try {
            val sample = res(header("sample"))
            val metabolite = res(header("metabolite"))
            val derivative = res(header("derivative"))
            val meanEnrichment = res(header("mean_enrichment")).toDouble

            extractInformatiponMetaboliteName(metabolite) match {
              case Some((metaboliteName, carbonStart, carbonEnd, fragmentName)) =>
                Some(IsocorValue(sample, derivative, metaboliteName, fragmentName, carbonStart, carbonEnd, meanEnrichment, experimental = true))
              case _ => None
            }

          } catch {
            case e: Exception =>
              System.err.println(e)
              None
          }
        }
      ).toSeq

    listEntries
  }
}
