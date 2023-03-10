# positional-carbon13-enrichment
[![CircleCI](https://dl.circleci.com/status-badge/img/gh/p2m2/positional-carbon13-enrichment/tree/develop.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/p2m2/positional-carbon13-enrichment/tree/develop)

https://p2m2.github.io/positional-carbon13-enrichment/

## install

### Node.js with JSDOM

check [scala.js installation page](https://www.scala-js.org/doc/project/js-environments.html)

```bash
runMain fr.inrae.p2m2.tools.PositionalCarbon13EnrichmentMain src/test/resources/galaxy430_res.tsv
```

### test app

```bash
sbt positionalCarbonSourcesJVM/test
sbt positionalCarbonSourcesJS/test
```

### command line

```shell
sbt positionalCarbonSourcesJVM/run jvm/src/test/resources/galaxy430_res.tsv
```

### jar

``` 
sbt positionalCarbonSourcesJVM/assembly
```

### Html

```shell 
sbt fastLinkJS 
```

```main 
fullOptJS
# --> js/target/scala-2.13/positionalcarbonsources-opt
```

open [index](./html/index.html)