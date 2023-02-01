package fr.inrae.p2m2.webapp

import org.scalajs.dom
import scalatags.JsDom.all._
import fr.inrae.p2m2.workflow.IsocorManagement

import scala.util.{Failure, Success, Try}

object PositionalCarbonMain {

  def main(args: Array[String]): Unit = {
    println("Hello world!")
    val res : String = Try(IsocorManagement.workflow(IsocorDataExample.exampleContentIsocor)) match {
      case Success(v) => println("OK") ;v.toString
      case Failure(exception) =>
        System.err.println(exception.getMessage)
        exception.getMessage
    }
    println(res)
    dom.document.getElementById("positionalCarbon13div").innerHTML = html(
      head(
        script(src := "..."),
        //script("alert('Hello World')")
      ),
      body(
        div(
          h1(id := "title", "This is a title"),
          p(res)
        )
      )
    ).render.innerHTML
  }
}
