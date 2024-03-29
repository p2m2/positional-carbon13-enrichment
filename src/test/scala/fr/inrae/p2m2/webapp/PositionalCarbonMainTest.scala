package fr.inrae.p2m2.webapp
import utest.{TestSuite, Tests, test}

object PositionalCarbonMainTest extends TestSuite {

  def tests: Tests = Tests {
    test("textPositionalEnrichmentDependencies empty map") {
      assert(PositionalCarbonMain.textPositionalEnrichmentDependencies(Map()) == "")
    }

    test("textPositionalEnrichmentDependencies") {
      val data = Map(
        "Compound" -> Seq(
          "C1" -> Seq("C1C2", "C2"),
          "C3" -> Seq("C1C3", "C1C2"),
        )
      )
      val text =
        """Compound -> C1 -> C1C2,C2
          |Compound -> C3 -> C1C3,C1C2
          |""".stripMargin.trim

      assert(PositionalCarbonMain.textPositionalEnrichmentDependencies(data) == text)
    }

    test("parsePositionalEnrichmentDependencies empty map") {
      assert(PositionalCarbonMain.parsePositionalEnrichmentDependencies("") == Map())
    }

    test("parsePositionalEnrichmentDependencies single rule Compound -> C1 -> C1C2,C2") {
      val waitingRes = Map(
        "Compound" -> Seq(
          "C1" -> Seq("C1C2", "C2")
        )
      )
      assert(PositionalCarbonMain.parsePositionalEnrichmentDependencies("Compound -> C1 -> C1C2,C2") == waitingRes)
    }
    test("parsePositionalEnrichmentDependencies multiple rule") {

      val waitingRes = Map(
        "Compound" -> Seq(
          "C1" -> Seq("C1C2", "C2"),
          "C3" -> Seq("C1C3", "C1C2"),
        )
      )

      val text =
        """
          |Compound -> C1 -> C1C2,C2
          |
          |Compound -> C3 -> C1C3,C1C2
          |""".stripMargin
      assert(PositionalCarbonMain.parsePositionalEnrichmentDependencies(text) == waitingRes)
    }

    test("Malate -> C2C3 -> C2C4, C4") {
      val waitingRes = Map(
        "Malate" -> Seq(
          "C2C3" -> Seq("C2C4", "C4")
        )
      )
      assert(PositionalCarbonMain.parsePositionalEnrichmentDependencies("Malate -> C2C3 -> C2C4, C4") == waitingRes)
    }

    test("Serine -> C1C3 -> C1, C2, C3") {
      val waitingRes = Map(
        "Serine" -> Seq(
          "C1C3" -> Seq("C1", "C2","C3")
        )
      )
      println(PositionalCarbonMain.parsePositionalEnrichmentDependencies("Serine -> C1C3 -> C1, C2, C3"))
      assert(PositionalCarbonMain.parsePositionalEnrichmentDependencies("Serine -> C1C3 -> C1, C2, C3") == waitingRes)

    }
  }
}