package fr.inrae.p2m2.tools

import fr.inrae.p2m2.build.BuildInfo

import java.io.File

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

  def process(config : Config) : Unit = {
    println("ok")
    val listMeanEnrichment = IsocorReader.getMeanEnrichmentByFragment(config.isocorOutputFile.head)
    listMeanEnrichment
      .groupBy(x=> (x.sample, x.derivative,x.metabolite) )
      .foreach{
        case (k,listValues : Seq[IsocorValue]) if listValues.distinct.size>1 =>
          println(k, listValues.distinct.size)
          /* setting with experimental values     CX...CZ =>  value,FRAGMENT  ""*/
          val mapArrangementCarbon13 : Map[String,(Double,String)]=
            listValues
              .distinct
              .map {
                case isocorVal => isocorVal.code-> (isocorVal.meanEnrichment,isocorVal.fragment)
              }.toMap

          println(mapArrangementCarbon13)
        case (k,_) => //println(k," => only 1 value")
      }

  }
}
