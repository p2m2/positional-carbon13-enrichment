package fr.inrae.p2m2.webapp

import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import js.JSConverters._

/**
 * Documentation Chart.js
 * https://www.chartjs.org/docs/latest/getting-started/
 */

@JSGlobal
@js.native
class Chart(ctx:dom.Element,obj:js.Dynamic) extends js.Object


case object Chart {
  def buildDataset(title:String, labels: Seq[String],
                   values_exp: Seq[Double],
                   values_computed: Seq[Double]): js.Dynamic = {

    js.Dynamic.literal(
      `type` = "bar",
      data = js.Dynamic.literal(
        labels = labels.toJSArray, //js.Array("Red", "Blue", "Yellow", "Green", "Purple", "Orange"),
        datasets = js.Array(
          js.Dynamic.literal(
            label = "Mean enrichment",
            `data` = (values_exp++values_computed).toJSArray, //js.Array(12.5, 19.2, 3, 5, 2, 3),
            backgroundColor =
              (values_exp.indices.map(_=>"rgba(54, 162, 235, 0.2)")
                  ++ values_computed.indices.map(_=>"rgba(255, 99, 132, 0.2)")).toJSArray,
            borderWidth = 1
          ),
/*
          js.Dynamic.literal(
            label = "calculated",
            `data` = values_computed.toJSArray, //js.Array(12.5, 19.2, 3, 5, 2, 3),
            backgroundColor = js.Array("rgba(255, 99, 132, 0.2)"),
            borderWidth = 1
          )*/
        ),
      ),
      options = js.Dynamic.literal(
        scales = js.Dynamic.literal(
          y = js.Dynamic.literal(
            beginAtZero = 0,
            max = 1.0,
            min = 0
          )
        ),
        plugins = js.Dynamic.literal(
          title = js.Dynamic.literal(
            display = true,
            text = title,
            color = "blue",
          )
        ),
      )
    )
  }
}