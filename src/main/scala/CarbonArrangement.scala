package fr.inrae.p2m2.tools

case object CarbonArrangement {
  /*
    p : size of Carbon arrangement
    n : number total of Carbon
   */
  def iteration(p: Int,n : Int) : Seq[String] = {
    p.to(n)
      .zipWithIndex
      .map {
        case (_, idx) =>
          1.to(p)
            .foldLeft("")((acc, y) => s"${acc}C${idx + y}")
      }
  }

  /*
      n : number total of Carbon
   */
  def get(n: Int) : Seq[String] = 1.to(n).flatMap(iteration(_,n))

  /**
   * iso2 should be > than iso1
   * compute theoretical mean enrichment between two isocor entries
   * @param iso1 first isocor entry
   * @param iso2 second isocor entry
   * @return theoretical mean enrichment
   */
  def computeDiffMeanEnrichment(iso1: IsocorValue,iso2: IsocorValue) : Double =
    (iso2.weight.toDouble * iso2.meanEnrichment) - (iso1.weight.toDouble * iso1.meanEnrichment)


  def computeAddMeanEnrichment(iso1: IsocorValue, iso2: IsocorValue): Double =
    ((iso2.weight.toDouble * iso2.meanEnrichment) + (iso1.weight.toDouble * iso1.meanEnrichment)) / (iso2.weight + iso1.weight).toDouble

  def fragment(frag1 : String, frag2 : String) : String =  (frag1,frag2) match {
    case (frag1,frag2) if frag1.nonEmpty && frag2.nonEmpty => frag1+"_"+frag2
    case (frag1,_) if frag1.nonEmpty  => frag1
    case (_,frag2) if frag2.nonEmpty  => frag2
    case (_,_) => ""
  }

  def buildNewDiffArrangement(iso1 : IsocorValue, iso2 : IsocorValue) : Option[(Int,Option[Int])] = {

    (iso1.carbonArrangementStart,
    iso1.carbonArrangementEnd.getOrElse(iso1.carbonArrangementStart),
      iso2.carbonArrangementStart,
      iso2.carbonArrangementEnd.getOrElse(iso2.carbonArrangementStart)) match {
        case(b1,e1,b2,e2) if b1==b2 && e1+1==e2 => Some(e1+1,None)
        case(b1,e1,b2,e2) if b1==b2 && e1+1<e2 => Some(e1+1,Some(e2))
        case (b1, e1, b2, e2) if e1 == e2 && b2 + 1 == b1 => Some(b2, None)
        case (b1, e1, b2, e2) if e1 == e2 && b2 + 1 < b1 => Some(b2, Some(b1-1))
        case(_,_,_,_) => None // a priori no rules ....
    }
  }

  def buildNewAddArrangement(iso1: IsocorValue, iso2: IsocorValue): Option[(Int, Option[Int])] = {

    ((iso1.carbonArrangementStart,
      iso1.carbonArrangementEnd.getOrElse(iso1.carbonArrangementStart),
      iso2.carbonArrangementStart,
      iso2.carbonArrangementEnd.getOrElse(iso2.carbonArrangementStart)) match {
      case (b1, e1, b2, e2) if e1+1 == b2 => Some(b1, Some(e2))
      case (b1, e1, b2, e2) if e2+1 == b1 => Some(b2, Some(e1))
      case (_, _, _, _) => None // a priori no rules ....
    }) match {
      case Some((v1,Some(v2))) if v1 == v2 => Some(v1,None)
      case opt => opt
    }
  }

  def computeDiff( iso1 : IsocorValue,iso2 : IsocorValue ) : Option[IsocorValue] = {
    val (v1, v2) = if (iso1.code.length > iso2.code.length) {
      (iso2, iso1)
    } else {
      (iso1, iso2)
    }
    buildNewDiffArrangement(v1,v2) match {
      case None => None
      case Some((begin,start)) =>
        Some(IsocorValue(v1.sample, v1.derivative, v1.metabolite,fragment(v1.fragment,v2.fragment),
          begin, start, computeDiffMeanEnrichment(v1,v2), experimental = false))
    }
  }

  def computeAdd( iso1 : IsocorValue,iso2 : IsocorValue ) : Option[IsocorValue] = {
    val (v1, v2) = if (iso1.code.length > iso2.code.length) {
      (iso2, iso1)
    } else {
      (iso1, iso2)
    }

    buildNewAddArrangement(iso1,iso2) match {
      case None => None
      case Some((begin, start)) =>
        Some(IsocorValue(v1.sample, v1.derivative, v1.metabolite, fragment(v1.fragment, v2.fragment),
          begin, start, computeAddMeanEnrichment(v1, v2), experimental = false))
    }
  }
}
