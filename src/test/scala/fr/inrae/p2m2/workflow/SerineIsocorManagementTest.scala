package fr.inrae.p2m2.workflow
import fr.inrae.p2m2.Data
import utest.{TestSuite, Tests, test}

import scala.util.{Success, Try}

object SerineIsocorManagementTest extends TestSuite {


  def tests: Tests = Tests {
    test("run process galaxy file") {
      val rules = Map(
        "Serine3" -> Seq(
          "C1" -> Seq("C1C2", "C2"),
          "C3" -> Seq("C2C3", "C2"),
          "C1C3" -> Seq("C1", "C2", "C3"),
        )
      )
      Try(IsocorManagement.workflow(Data.contentSerine, rules)) match {
        case Success(newData) =>
          val listDataResults = newData.flatMap {
            case (_ :SampleAndMetabolite, list : Seq[DataResults]) =>
              list.filter( (d : DataResults) => !d.experimental )
          }

          assert(listDataResults.size == 5)
        case _ => assert(false)
      }
    }
  }
}
