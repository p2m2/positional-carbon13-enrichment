package fr.inrae.p2m2.webapp

import utest.{TestSuite, Tests, test}

object PositionalCarbonMainTest extends TestSuite {
  def tests: Tests = Tests {
    test("textPositionalEnrichmentDependencies empty map") {
      assert(PositionalCarbonMain.textPositionalEnrichmentDependencies(Map()) == "")
    }
    test("parsePositionalEnrichmentDependencies empty map") {
      assert(PositionalCarbonMain.parsePositionalEnrichmentDependencies("") == Map())
    }

    test("parsePositionalEnrichmentDependencies single rule Compound -> (C1 -> C1C2,C2)") {
      val waitingRes = Map(
        "Compound" -> Seq(
          "C1" -> Seq("C1C2", "C2")
        )
      )
      assert(PositionalCarbonMain.parsePositionalEnrichmentDependencies("Compound -> (C1 -> C1C2,C2)") == waitingRes)
    }
    test("parsePositionalEnrichmentDependencies multiple rule") {
      val text =
        """
          |Compound -> (C1 -> C1C2,C2)
          |Compound -> (C3 -> C1C3,C1C2)
          |""".stripMargin
      println(PositionalCarbonMain.parsePositionalEnrichmentDependencies(text))
    }

  }
}