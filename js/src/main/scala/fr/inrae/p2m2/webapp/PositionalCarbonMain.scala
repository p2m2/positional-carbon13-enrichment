package fr.inrae.p2m2.webapp

import scala.scalajs.js
import org.scalajs.dom
import scalatags.JsDom.all.{id, _}
import fr.inrae.p2m2.workflow.IsocorManagement
import org.scalajs.dom.{Event, FileReader, HTMLInputElement, window}
import org.scalajs.dom.html.{Canvas, Element, Input}
import scalatags.JsDom

import scala.collection.immutable.Map
import scala.scalajs.js.JSON
import scala.scalajs.js.URIUtils.encodeURIComponent
import scala.util.{Failure, Success, Try}

object PositionalCarbonMain {

  /* Default dependencies to compute enrichment mean */
  val initialData : Map[String,Seq[(String,Seq[String])]] = Map(
    "Glutamate" ->
      Seq("C1" -> Seq("C1C5","C2C5"))
    ,
    "Alphaalanine" -> Seq(
        "C1C3" -> Seq("C1","C2C3"),
        "C2"-> Seq("C1C2","C1"),
        "C3"-> Seq("C2C3","C2")),
    "Serine3" -> Seq(
      "C1" -> Seq("C1C2","C2"),
      "C3" -> Seq("C2C3","C2"), // bug ne devrait pas etre calculé avec C1C3 (methode B)
      "C1C3" -> Seq("C1","C2","C3"),
     )
  )

  val inputTagId : String = "positionInputFile"
  val idMainDiv : String = "positionalCarbonChartCanvas"
  val idDisplay : String = "display"
  val idHeader : String = "header"

  def parsePositionalEnrichmentDependencies(s : String) : Map[String,Seq[(String,Seq[String])]] = {
    //val numberPattern = "a-zA-Z".r
    Map()
  }

  def textPositionalEnrichmentDependencies( m : Map[String,Seq[(String,Seq[String])]]) : String = {
    m.map {
      case (compose : String, rules : Seq[(String,Seq[String])] ) =>
        compose + " -> (" + rules.map {
          case (toCompute : String, dependencies : Seq[String]) =>
             toCompute + " -> " + dependencies.mkString(",")
        }.mkString("\n") + ")"
    }.mkString("\n")
  }

  def buildCanvasBarPlot(idBarPlot: String): JsDom.TypedTag[Canvas] =
    canvas(id := idBarPlot)


  def appendCanvas(idDivSample:String,idDiv:String,idCanvas:String) : Unit =
    dom
      .document
      .getElementById(idDivSample)
      .append(
        div( id:=idDiv ,
          `class` := "canvasChart",
          buildCanvasBarPlot(idCanvas) )
          .render)

  def setPositionalEnrichmentDependencies(s : String) = {
    dom
      .document
      .getElementById("positionalEnrichmentDependencies")
      .innerText = s
  }

  def cleanHtmlPage : Unit = {
    Try(dom.document.getElementById(idHeader).remove()) match {
      case _ =>
    }
    Try(dom.document.getElementById(idMainDiv).remove()) match {
      case _ =>
    }

    dom
      .document
      .getElementById(idDisplay)
      .append(
        div(id:=idMainDiv).render
      )
  }

  def updateHtmlPage(content : String) : Unit = {
    cleanHtmlPage
    Try(IsocorManagement.workflow(content.trim,initialData)) match {
      case Success(v) => {
        val textContent : String = {
          "Sample\tMetabolite\tIsotope\tMean\tExperiment/Computed\n" +
          v.map {
          case (k,l) => l.map( u => Seq(k._1,k._2, u._1, u._2, u._3).mkString("\t") ).mkString("\n")
        }.mkString("\n")
        }

        dom
          .document
          .getElementById(idMainDiv)
          .append(
            div(
              a("download C-Positional Enrichments (TSV file)",href:="data:text/tsv;charset=UTF-8,"+encodeURIComponent(textContent))
            ).render
          )

        val metabolites_with_me: Map[(String, String), Seq[(String, Double, Boolean)]] = v
        metabolites_with_me
          .groupBy(_._1._1)
          .foreach {
            case ((sample, listV)) =>
              val idDivSample = s"div_$sample"
              dom
                .document
                .getElementById(idMainDiv)
                .append(
                  div(
                    h2(s"Sample $sample"),
                    div(id:=idDivSample, `class`:="gridCanvas")
                  ).render
                )

              listV.foreach {
                case ((sample, metabolite), data) if data.nonEmpty =>
                  val idDiv: String = sample + "_" + metabolite + "_" + "_div"
                  val idCanvas: String = sample + "_" + metabolite + "_" + "_canvas"
                  val title = metabolite

                  val values_exp = data.filter(_._3).map(_._2)
                  val labels_exp = data.filter(_._3).map(_._1)

                  val values_calc = data.filter(!_._3).map(_._2)
                  val labels_calc = data.filter(!_._3).map(_._1)

                  appendCanvas(idDivSample,idDiv, idCanvas)

                  val ctx = dom.document.getElementById(idCanvas)
                  new Chart(ctx, Chart.buildDataset(title,labels_exp, values_exp,labels_calc,values_calc))
                case _ => println("ok")
              }
          }
      }
      case Failure(exception) =>
        dom.document
          .getElementById (idMainDiv)
          .append(
            div(
              h1(id := "Error Message", " - Exception - "),
              p(exception.getMessage)
            ).render
          )
    }
  }

  def main(args: Array[String]): Unit = {

    setPositionalEnrichmentDependencies(textPositionalEnrichmentDependencies(initialData))

    val inputTag: JsDom.TypedTag[Input] = input(
      id := "isocorInputFile",
      `type` := "file",
      onchange := {
        () =>

          val tag = dom.document.getElementById("isocorInputFile")
          val files = tag.render.asInstanceOf[HTMLInputElement].files

          if (files.nonEmpty) {
            val reader = new FileReader();
            reader.onload = (_ : Event) => {
              val content = reader.result.toString
              updateHtmlPage(content)
            }
         //   println(s"reading ${files(0).name}")
            reader.readAsText(files(0));
          }

      }
    )

    dom
      .document
      .getElementById(inputTagId)
      .append(inputTag.render)

    }
}
