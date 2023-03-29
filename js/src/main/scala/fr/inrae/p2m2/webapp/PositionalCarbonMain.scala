package fr.inrae.p2m2.webapp

import scala.scalajs.js
import org.scalajs.dom
import scalatags.JsDom.all._
import fr.inrae.p2m2.workflow.IsocorManagement
import org.scalajs.dom.{Event, FileReader, HTMLInputElement, window}
import org.scalajs.dom.html.{Canvas, Element, Input}
import scalatags.JsDom

import scala.scalajs.js.JSON
import scala.scalajs.js.URIUtils.encodeURIComponent
import scala.util.{Failure, Success, Try}

object PositionalCarbonMain {


  val initialData : Map[String,Seq[String]] = Map(
    "Glutamate" -> Seq("C1"),
    "Serine3" -> Seq("C1", "C1C3", "C3")
  )

  val inputTagId : String = "positionInputFile"
  val idMainDiv : String = "positionalCarbonChartCanvas"

  def buildCanvasBarPlot(idBarPlot: String): JsDom.TypedTag[Canvas] =
    canvas(id := idBarPlot)


  def appendCanvas(idDivSample:String,idDiv:String,idCanvas:String) =
    dom
      .document
      .getElementById(idDivSample)
      .append(
        div( id:=idDiv ,
          `class` := "canvasChart",
          buildCanvasBarPlot(idCanvas) )
          .render)

  def updateHtmlPage(content : String) = {
    dom.document.getElementById(idMainDiv).innerHTML =
      div(
        id:=idMainDiv,
        h1("Fractional mean ",sup("13"),"C enrichment")
    ).render.innerHTML

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
              a("download mean as a TSV file",href:="data:text/tsv;charset=UTF-8,"+encodeURIComponent(textContent))
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



                  //println("****************")
                  //println(sample,metabolite)
                  //println("EXP")
                 // data.filter(_._3).foreach{ elt => println(elt)}
                 // println("CALC")
                 // values_calc.foreach{ elt => println(elt)}

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
