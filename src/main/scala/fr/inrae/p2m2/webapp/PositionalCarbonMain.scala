package fr.inrae.p2m2.webapp

import scala.scalajs.js
import org.scalajs.dom
import scalatags.JsDom.all.{id, _}
import fr.inrae.p2m2.workflow.IsocorManagement
import org.scalajs.dom.{Event, FileReader, HTMLInputElement, HTMLTextAreaElement, window}
import org.scalajs.dom.html.{Canvas, Element, Input}
import org.scalajs.dom.window.alert
import scalatags.JsDom

import scala.collection.immutable.Map
import scala.scalajs.js.JSON
import scala.scalajs.js.URIUtils.encodeURIComponent
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex

object PositionalCarbonMain {

  type ComputeRulesType = Map[String,Seq[(String,Seq[String])]]
  /* Default dependencies to compute enrichment mean */
  val initialData : ComputeRulesType = Map(
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

  def clean(el : String) : String =
    el
      .replace(",","")
      .replace(" ","")
      .replace("\t","")
      .replace("\n","")
  def parsePositionalEnrichmentDependencies(s : String) : ComputeRulesType = {

    val sep1 = "\\s*->\\s*"

    val keyValPattern: Regex = s"([\\da-zA-Z]+)$sep1\\s*([C\\d]+)$sep1([C\\d]+)\\s*(,\\s*[C\\d]+\\s*)?".r

    keyValPattern.findAllMatchIn(s).toSeq.flatMap {
      case (data : Regex.Match) if data.groupCount>2 => {
        // 1 -> Metabolite
        // 2 -> Isotope to compute
        // 3..n -> Dependencies
     //  println(data.groupCount)
     //   println("*"+data.group(3)+"*")
     //   println("*"+data.group(4)+"*")
        Some(clean(data.group(1)) ->
          (Seq( clean(data.group(2)) -> 3.to(data.groupCount)
          .map(a => clean(data.group(a))))))
        // println(group.mkString(","))
      }
      //println(s"key: *${patternMatch.group(1)}* value: *${patternMatch.group(2)}* ${patternMatch.groupCount}")
      case (data : Regex.Match) =>
        System.err.println(s"Can not parse ${data.source}")
        None
    }
      .groupBy(_._1)
      .map {
        case (compound, struct) =>  compound -> struct.flatMap(_._2)
      }
  }

  def textPositionalEnrichmentDependencies( m : ComputeRulesType) : String = {
    m.map {
      case (compose : String, rules : Seq[(String,Seq[String])] ) =>
         rules.map {
          case (toCompute : String, dependencies : Seq[String]) =>
            compose + " -> " + toCompute + " -> " + dependencies.mkString(",")
        }.mkString("\n")
    }.mkString("\n")
  }

  def buildCanvasBarPlot(idBarPlot: String): JsDom.TypedTag[Canvas] =
    canvas(id := idBarPlot)


  def appendCanvas(idDivSample:String,idDiv:String,idCanvas:String) : Unit =
    if (dom.document != null) {
      dom
      .document
      .getElementById(idDivSample)
      .append(
        div( id:=idDiv ,
          `class` := "canvasChart",
          buildCanvasBarPlot(idCanvas) )
          .render)
    }

  def setPositionalEnrichmentDependencies(s : String) = {
    dom
      .document
      .getElementById("positionalEnrichmentDependencies")
      .asInstanceOf[HTMLTextAreaElement].value = s
  }

  def cleanHtmlPage() : Unit = {
    if (dom.document != null) {
      Try(dom.document.getElementById(idMainDiv).remove()) match {
        case _ =>
      }

      dom
        .document
        .getElementById(idDisplay)
        .append(
          div(id := idMainDiv).render
        )
    }
  }

  def buildCharts(metabolites_with_me: Map[(String, String), Seq[(String, Double, Boolean)]]) : Unit = {
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
                  div(id := idDivSample, `class` := "gridCanvas")
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

              appendCanvas(idDivSample, idDiv, idCanvas)

              if (dom.document != null)  {
                val ctx = dom.document.getElementById(idCanvas)
                new Chart(ctx, Chart.buildDataset(title, labels_exp, values_exp, labels_calc, values_calc))
              }

            case _ => println("ok")
          }
      }
  }

  def updateHtmlPage(content : String, rulesForEachMetabolite : ComputeRulesType) : Unit = {
    cleanHtmlPage

    println("==== rules =====")
    println(rulesForEachMetabolite)

    Try(IsocorManagement.workflow(content.trim,rulesForEachMetabolite)) match {
      case Success(v) =>
        val textContent : String = {
          "Sample\tMetabolite\tIsotope\tMean\tExperiment/Computed\n" +
          v.map {
          case (k,l) => l.map( u => Seq(k._1,k._2, u._1, u._2, u._3).mkString("\t") ).mkString("\n")
        }.mkString("\n")
        }
        if (dom.document != null) {
          dom
            .document
            .getElementById(idMainDiv)
            .append(
              div(
                h3("Results files to download"),
                ul(
                  li(
                    a("C-Positional Enrichments (TSV file)", href := "data:text/tsv;charset=UTF-8," + encodeURIComponent(textContent))
                  )
                )
              ).render
            )
        }
        buildCharts(v)

      case Failure(exception) =>
          dom.document
            .getElementById(idMainDiv)
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
          val contentRules = dom.document.getElementById("positionalEnrichmentDependencies").asInstanceOf[HTMLTextAreaElement].value
          //alert(contentRules)
          if (files.nonEmpty) {
            val reader = new FileReader();
            reader.onload = (_ : Event) => {
              val content = reader.result.toString
              val rules : ComputeRulesType = parsePositionalEnrichmentDependencies(contentRules)
              updateHtmlPage(content,rules)
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
