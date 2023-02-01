package fr.inrae.p2m2.tools

case class IsocorValue(
                        sample: String,
                        derivative: String,
                        metabolite: String,
                        fragment: String,
                        carbonArrangementStart: Int,
                        carbonArrangementEnd: Option[Int],
                        meanEnrichment: Double,
                        experimental: Boolean
                      ) {

  def code: String = carbonArrangementEnd match {
    case Some(end) if end > carbonArrangementStart => s"C${carbonArrangementStart}C${end}" //carbonArrangementStart.to(end).map(x=>s"C$x").mkString("")
    case _ => s"C$carbonArrangementStart"
  }

  def weight: Int = carbonArrangementEnd match {
    case None => 1
    case Some(end) => end - carbonArrangementStart + 1
  }

}
