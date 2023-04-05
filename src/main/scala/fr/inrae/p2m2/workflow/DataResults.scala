package fr.inrae.p2m2.workflow
//CODE_FRAG, MEAN_ENR, EXPERIMENTAL
case class DataResults(
                        codeFrag : String,
                        mean : Double,
                        experimental : Boolean,
                        predecessors : Seq[String]
                      )
