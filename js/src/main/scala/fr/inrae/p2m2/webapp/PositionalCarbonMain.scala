package fr.inrae.p2m2.webapp

import scala.scalajs.js
import org.scalajs.dom
import scalatags.JsDom.all._
import fr.inrae.p2m2.workflow.IsocorManagement
import org.scalajs.dom.{Event, FileReader, HTMLInputElement}
import org.scalajs.dom.html.{Canvas, Element, Input}
import scalatags.JsDom

import scala.scalajs.js.JSON
import scala.util.{Failure, Success, Try}

object PositionalCarbonMain {
  val inputTagId : String = "positionInputFile"
  val idMainDiv : String = "positionalCarbonChartCanvas"

  def buildCanvasBarPlot(idBarPlot: String): JsDom.TypedTag[Canvas] =
    canvas(id := idBarPlot)


  def appendCanvas(idDiv:String,idCanvas:String, title:String) =
    dom
      .document
      .getElementById(idMainDiv)
      .append(
        div( id:=idDiv , `class` := "canvasChart", h2(title), buildCanvasBarPlot(idCanvas) )
          .render)

  def updateHtmlPage(content : String) = {
    println(content)
    dom.document.getElementById(idMainDiv).innerHTML = div( id:=idMainDiv ).render.innerHTML

    Try(IsocorManagement.workflow(content.trim)) match {
      case Success(v) => {
        val metabolites_with_me: Map[(String, String), Seq[(String, Double, Boolean)]] = v
        metabolites_with_me
          .groupBy(_._1._1)
          .foreach {
            case ((sample, listV)) =>
              dom
                .document
                .getElementById(idMainDiv)
                .append(h1(sample).render)

              listV.foreach {
                case ((sample, metabolite), data) if data.nonEmpty =>
                  val idDiv: String = sample + "_" + metabolite + "_" + "_div"
                  val idCanvas: String = sample + "_" + metabolite + "_" + "_canvas"
                  val title = metabolite

                  val labels = data.map(_._1)
                  val values = data.map(_._2)

                  val backgroundColor = data.map {
                    case (_, _, true) => "rgba(54, 162, 235, 0.2)"
                    case (_, _, false) => "rgba(255, 99, 132, 0.2)"
                  }

                  appendCanvas(idDiv, idCanvas, title)

                  val ctx = dom.document.getElementById(idCanvas)
                  println(ctx)
                  new Chart(ctx, Chart.buildDataset(labels, values, backgroundColor))
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
            reader.onload = (event : Event) => {
              val content = reader.result.toString
              updateHtmlPage(content)
            }
            println(s"reading ${files(0).name}")
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
