# Calculations of <sup>13</sup>C-Positional Enrichments

[![p2m2](https://circleci.com/gh/p2m2/positional-carbon13-enrichment.svg?style=shield)](https://app.circleci.com/pipelines/github/p2m2)
[![codecov](https://codecov.io/gh/p2m2/positional-carbon13-enrichment/branch/develop/graph/badge.svg)](https://codecov.io/gh/p2m2/positional-carbon13-enrichment)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/435ffc5c2a1e40ed9deb031877eda9ce)](https://app.codacy.com/gh/p2m2/positional-carbon13-enrichment/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![web](https://img.shields.io/badge/Web-Online-blue.svg)](https://p2m2.github.io/positional-carbon13-enrichment/)
[![doi](https://img.shields.io/badge/doi-10.3390/metabo13040466-blue.svg)](https://doi.org/10.3390/metabo13040466)

<sup>13</sup>C-positional enrichments were calculated by combining mean <sup>13</sup>C enrichments
of several mass fragments that shared part of the carbon backbone from the same TMS
derivative. A workflow has been created to calculate these <sup>13</sup>C-positional enrichments
directly from the outputs of our <sup>13</sup>C-processing method

To use the software without prior installation and obtain a visual rendering, please visit the site https://p2m2.github.io/positional-carbon13-enrichment/

For a precise description of the method, refer to the publication [*"Evaluation of GC/MS-Based <sup>13</sup>C-Positional Approaches for TMS Derivatives of Organic and Amino Acids and Application to Plant <sup>13</sup>C-Labeled Experiments"*](https://doi.org/10.3390/metabo13040466)

## Software programming environment

- [SBT](https://www.scala-sbt.org/)
- [Scala](https://www.scala-lang.org/)
- [ScalaJS - Node.js with JSDOM](https://www.scala-js.org/doc/project/js-environments.html)


### Dependencies for HTML generation

- [Chart.js](https://www.chartjs.org/)
- [ScalaTags](https://com-lihaoyi.github.io/scalatags/)

### command line

```shell
sbt positionalCarbonSourcesJVM/run ./resources/galaxy430_res.tsv
```

### Html

#### Development version

```shell 
sbt fastLinkJS 
# open html/index.html
```

#### Release

```shell 
sbt fullOptJS
cp js/target/scala-2.13/positionalcarbonsources-opt/* docs/
# open docs/index.html
```

## Information about the authors and the software

*Y. Dellero, O. Filangi, A . Bouchereau*

- [Institute for Genetics, Environment and Plant Protection (IGEPP), National Research Institute for Agriculture, Food and Environment (INRAE), Institut Agro, Université Rennes, 35650 Le Rheu, France](https://www6.rennes.inrae.fr/igepp)
- [Metabolic Profiling and Metabolomic Platform (P2M2), Biopolymers Interactions Assemblies, Institute for Genetics, Environment and Plant Protection, 35650 Le Rheu, France](https://www6.inrae.fr/p2m2/)
- [MetaboHUB, National Infrastructure of Metabolomics and Fluxomics, 35650 Le Rheu, France](https://www.metabohub.fr/)


This code is released under the MIT License.



