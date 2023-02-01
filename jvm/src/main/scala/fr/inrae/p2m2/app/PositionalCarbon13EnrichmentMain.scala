package fr.inrae.p2m2.app

import buildinfo.BuildInfo
import fr.inrae.p2m2.workflow.IsocorManagement

import java.io.File
import scala.io.Source

case object PositionalCarbon13EnrichmentMain extends App {

      import scopt.OParser

      case class Config(
                         isocorOutputFile: Seq[File] = Seq()
                       )

      val builder = OParser.builder[Config]
      val parser1 = {
            import builder._
            OParser.sequence(
                  programName(BuildInfo.name),
                  head(BuildInfo.name, BuildInfo.version),
                  arg[File]("<file>...")
                    .unbounded()
                    .action((x, c) => c.copy(isocorOutputFile = c.isocorOutputFile :+ x)),
                  help("help").text("prints this usage text"),
                  note("some notes." + sys.props("line.separator")),
                  checkConfig(_ => success)
            )
      }

      OParser.parse(parser1, args, Config()) match {
            case Some(config) =>
                  process(config)
            // do something
            case _ => System.err.println("Ko")
            // arguments are bad, error message will have been displayed
      }


      def process(config: Config): Unit = {
            println("ok")
            val fileTest = config.isocorOutputFile.head
            val source = Source.fromFile(fileTest)
            val content : String = source.getLines().mkString("\n")
            IsocorManagement.workflow(content)

      }
      println("ok")

}
