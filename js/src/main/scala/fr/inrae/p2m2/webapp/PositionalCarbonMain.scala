package fr.inrae.p2m2.webapp

import scala.scalajs.js
import org.scalajs.dom
import scalatags.JsDom.all._
import fr.inrae.p2m2.workflow.IsocorManagement
import org.scalajs.dom.html.{Canvas, Element}
import scalatags.JsDom

import js.JSConverters._
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSGlobal
import scala.util.{Failure, Success, Try}

@JSGlobal
@js.native
class Chart(ctx:dom.Element,obj:js.Dynamic) extends js.Object
object PositionalCarbonMain {

  val idMainDiv : String = "positionalCarbonChartCanvas"

  def buildDataset(labels : Seq[String],values : Seq[Double]) : js.Dynamic = {
    println(labels)
    println(values)

    js.Dynamic.literal(
      `type` = "bar",
      data = js.Dynamic.literal(
        labels = labels.toJSArray, //js.Array("Red", "Blue", "Yellow", "Green", "Purple", "Orange"),
        datasets = js.Array(
          js.Dynamic.literal(
            label = "Fractional mean 13C Enrichment",
            `data` = values.toJSArray, //js.Array(12.5, 19.2, 3, 5, 2, 3),
            backgroundColor =
              js.Array(
                "rgba(255, 99, 132, 0.2)",
                "rgba(255, 159, 64, 0.2)",
                "rgba(255, 10, 64, 0.2)",
                "rgba(255, 19, 64, 0.2)",
                "rgba(255, 169, 64, 0.2)",
                "rgba(255, 77, 64, 0.2)"),
            borderWidth = 1
          )
        ),
      ),
      options = js.Dynamic.literal(
        scales = js.Dynamic.literal(
          y = js.Dynamic.literal(beginAtZero = 0)
        )
      )
    )
  }

  def buildCanvasBarPlot(idBarPlot: String): JsDom.TypedTag[Canvas] =
    canvas(id := idBarPlot)


  def appendCanvas(idDiv:String,idCanvas:String, title:String) =
    dom
      .document
      .getElementById(idMainDiv)
      .append(
        div( id:=idDiv , `class` := "canvasChart", h2(title), buildCanvasBarPlot(idCanvas) )
          .render)

  def main(args: Array[String]): Unit = {
    println("Hello world!")
   // test
    Try(IsocorManagement.workflow(IsocorDataExample.exampleContentIsocor)) match {
      case Success(v) => {
        val metabolites_with_me : Map[(String,String), Map[String,Double]] = v
        metabolites_with_me
          .groupBy(_._1._1 )
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

                  val labels = data.keys.toSeq
                  val values = data.values.toSeq

                  appendCanvas(idDiv, idCanvas, title)

                  val ctx = dom.document.getElementById(idCanvas)
                  println(ctx)
                  new Chart(ctx, buildDataset(labels, values))
                case _ => println("ok")
              }
          }
      }
      case Failure(exception) =>
        dom.document.body.innerHTML = html(
          head(
            script(src := "..."),
            //script("alert('Hello World')")
          ),
          body(
            div(
              h1(id := "Error Message", " - Exception - "),
              p(exception.getMessage)
            )
          )
        ).render.innerHTML
    }


  }
}
