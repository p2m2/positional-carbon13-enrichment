package fr.inrae.p2m2.tools

import utest.{TestSuite, Tests, test}

object IsocorValueTest extends TestSuite {

  def tests: Tests = Tests {
    test("code 1") {

      assert(IsocorValue("a","b","c","d",3,Some(-1),0.0,experimental = true).code == "C3")
    }

    test("code 2") {
      assert(IsocorValue("a", "b", "c", "d", 3, None, 0.0,experimental = true).code == "C3")
    }

    test("code 3") {
      assert(IsocorValue("a", "b", "c", "d", 3, Some(3), 0.0,experimental = true).code == "C3")
    }

    test("code 4") {
      assert(IsocorValue("a", "b", "c", "d", 3, Some(4), 0.0,experimental = true).code == "C3C4")
    }

    test("code 5") {
      assert(IsocorValue("a", "b", "c", "d", 2, Some(6), 0.0,experimental = true).code == "C2C3C4C5C6")
    }
  }

}
