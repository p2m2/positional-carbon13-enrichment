package fr.inrae.p2m2.webapp

import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import js.JSConverters._

@JSGlobal
@js.native
class Chart(ctx:dom.Element,obj:js.Dynamic) extends js.Object

case object Chart {
  def buildDataset(labels: Seq[String], values: Seq[Double], backgroundColor: Seq[String]): js.Dynamic = {

    js.Dynamic.literal(
      `type` = "bar",
      data = js.Dynamic.literal(
        labels = labels.toJSArray, //js.Array("Red", "Blue", "Yellow", "Green", "Purple", "Orange"),
        datasets = js.Array(
          js.Dynamic.literal(
            label = "Fractional mean 13C Enrichment",
            `data` = values.toJSArray, //js.Array(12.5, 19.2, 3, 5, 2, 3),
            backgroundColor = backgroundColor.toJSArray,
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
}