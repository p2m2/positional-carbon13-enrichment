package fr.inrae.p2m2.tools

case object CarbonArrangement {
  /*
    p : size of Carbon arrangement
    n : number total of Carbon
    startCarbonNum : when starting with carbon index != 1 example C3C4C5
   */
  def iteration(p: Int, n: Int, startCarbonNum: Int = 1): Seq[String] = {
    p.to(n)
      .zipWithIndex
      .map {
        case (_, idx) =>
          startCarbonNum.until(p + startCarbonNum)
            .foldLeft("")((acc, y) => s"${acc}C${idx + y}")
      }
  }

  /*
      n : number total of Carbon
   */
  def getArrangements(n: Int): Seq[String] = 1.to(n).flatMap(iteration(_, n))

  /*
      Execution plan to compute new value

      C1C2C3

      could provided with sum :
        - C1 + C2 + C3
        - C1C2 + C3
        - C1 + C2C3

      could provided with diff :
        - C1C2C3C4 - C4
        - C1C2C3C4C5 - C4C5
        - ...
   */


  /**
   * String code with carbon to internal structure
   * - C1 => Some(1,1)
   * - C1C4 => Some(1,4)
   * - impossibles values (C1C0,...) => None
   *
   * @param code carbon code name
   * @return an option of start and end index
   */
  def code2Indexes(code: String): Option[(Int, Int)] = {
    val pattern = "C(\\d{0,2})\\w*C(\\d{0,2})$".r
    val pattern2 = "C(\\d+)$".r

    //val pattern(firstCarbon) = code

    code match {
      case pattern(firstCarbon, lastCarbon) if (firstCarbon.toInt < lastCarbon.toInt) =>
        Some(firstCarbon.toInt, lastCarbon.toInt)
      case pattern2(firstCarbon) =>
        Some(firstCarbon.toInt, firstCarbon.toInt)
      case _ => None
    }
  }

  def weight(code: String): Double = code2Indexes(code: String) match {
    case None => 0.0
    case Some((start, end)) => end - start + 1
  }

  /**
   * From internal structure (start and end index of Carbon code) get a string carbon code
   * - Some(1,1) => C1
   * - Some(1,4) => C1C4
   * - impossibles values => None
   *
   * @param indexes option of start and end index
   * @return the carbon code
   */
  def indexes2code(indexes: Option[(Int, Int)]): Option[String] = indexes match {
    case Some((start, end)) if start > 0 && start == end => Some(s"C$start")
    case Some((start, end)) if start > 0 && start < end => Some(s"C${start}C${end}")
    case Some((start, end)) if start > end => None
    case _ => None
  }

  def diffCode(code1: String, code2: String): Option[String] = {
    (code2Indexes(code1), code2Indexes(code2)) match {
      case (Some((s1, e1)), Some((s2, e2))) if (s1 == s2) && e1 <= e2 => indexes2code(Some(e1 + 1, e2))
      case (Some((s1, e1)), Some((s2, e2))) if (s1 == s2) && e2 <= e1 => indexes2code(Some(e2 + 1, e1))
      case (Some((s1, e1)), Some((s2, e2))) if (e1 == e2) && s1 <= s2 => indexes2code(Some(e1 + 1, e2))
      case (Some((s1, e1)), Some((s2, e2))) if (e1 == e2) && s2 <= s1 => indexes2code(Some(e2 + 1, e1))
      case _ => None
    }
  }

  /**
   * Get execution plan to build in silico value from experimental and computed fragment value
   * planning("C1C4") => C1 + planning("C2C4")
   *
   * @param code
   * @return
   */
  def planningComputedAdditionalValues(code: String): Seq[(String, Seq[String])] = {
    (code2Indexes(code) match {
      case Some((first, last)) if first < last =>
        1.to(last - first).
          flatMap(
            idx => iteration(idx, last - first + 1, first)
              .flatMap(left => diffCode(left, code) match {
                case Some(right) =>
                  val v1: Seq[(String, Seq[String])] = planningComputedAdditionalValues(left)
                  val v2: Seq[(String, Seq[String])] = planningComputedAdditionalValues(right)
                  val r1: Seq[(String, Seq[String])] = v2.map(x => (code, left +: x._2))
                  val r2: Seq[(String, Seq[String])] = v1.map(x => (code, x._2 :+ right))

                  r1 ++ r2 //++ v1 ++ v2
                case None => None
              })
          )
      case Some((first, last)) if first == last => Seq((code, Seq(s"C$first")))
      case _ => Seq()
    }).distinct
  }


  /**
   * iso2 should be > than iso1
   * compute theoretical mean enrichment between two isocor entries
   *
   * @param iso1 first isocor entry
   * @param iso2 second isocor entry
   * @return theoretical mean enrichment
   */
  def computeDiffMeanEnrichment(iso1: IsocorValue, iso2: IsocorValue): Double =
    (iso2.weight.toDouble * iso2.meanEnrichment) - (iso1.weight.toDouble * iso1.meanEnrichment)


  def computeAddMeanEnrichment(iso1: IsocorValue, iso2: IsocorValue): Double =
    ((iso2.weight.toDouble * iso2.meanEnrichment) + (iso1.weight.toDouble * iso1.meanEnrichment)) / (iso2.weight + iso1.weight).toDouble

  /**
   *
   * @param isos
   * @return
   */
  def sumMeanEnrichment(isos: Seq[(Double, Double)], nbCarbon: Double): Double =
    isos.map(iso => iso._1 * iso._2).sum / nbCarbon

  def diffMeanEnrichment(isoSum: (Double, Double), isos: Seq[(Double, Double)], nbCarbon: Double): Double =
    ((isoSum._1 * isoSum._2) - (isos.map(iso => iso._1 * iso._2).sum)) / nbCarbon

  def fragment(frag1: String, frag2: String): String = (frag1, frag2) match {
    case (frag1, frag2) if frag1.nonEmpty && frag2.nonEmpty => frag1 + "_" + frag2
    case (frag1, _) if frag1.nonEmpty => frag1
    case (_, frag2) if frag2.nonEmpty => frag2
    case (_, _) => ""
  }

}
