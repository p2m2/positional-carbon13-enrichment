# Calculations of <sup>13</sup>C-Positional Enrichments

[![p2m2](https://circleci.com/gh/p2m2/positional-carbon13-enrichment.svg?style=shield)](https://app.circleci.com/pipelines/github/p2m2)
[![codecov](https://codecov.io/gh/p2m2/positional-carbon13-enrichment/branch/develop/graph/badge.svg)](https://codecov.io/gh/p2m2/positional-carbon13-enrichment)
[![web](https://img.shields.io/badge/Web-Online-blue.svg)](https://p2m2.github.io/positional-carbon13-enrichment/)
[![doi](https://img.shields.io/badge/doi-10.3390/metabo13040466-blue.svg)](https://doi.org/10.3390/metabo13040466)

<sup>13</sup>C-positional enrichments were calculated by combining mean <sup>13</sup>C enrichments
of several mass fragments that shared part of the carbon backbone from the same TMS
derivative. A workflow has been created to calculate these <sup>13</sup>C-positional enrichments
directly from the outputs of our <sup>13</sup>C-processing method

To use the software without prior installation and obtain a visual rendering, please visit the site https://p2m2.github.io/positional-carbon13-enrichment/

For a precise description of the method, refer to the publication [*"Evaluation of GC/MS-Based <sup>13</sup>C-Positional Approaches for TMS Derivatives of Organic and Amino Acids and Application to Plant <sup>13</sup>C-Labeled Experiments"*](https://doi.org/10.3390/metabo13040466)

## Software installation
### Dependencies
- [SBT](https://www.scala-sbt.org/)
- [ScalaJS - Node.js with JSDOM](https://www.scala-js.org/doc/project/js-environments.html)

### command line

```shell
sbt positionalCarbonSourcesJVM/run jvm/src/test/resources/galaxy430_res.tsv
```

### Html

```shell 
sbt fastLinkJS 
```

```sbt 
sbt fullOptJS
cp js/target/scala-2.13/positionalcarbonsources-opt/* docs/
# the html page is available in the docs directory
```

## Authors
- Y. Dellero, O. Filangi - IGEPP’s Metabolic Profiling and Metabolomic Platform (P2M2, Rennes)


This code is released under the MIT License.



