# positional-carbon13-enrichment

##test

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

open [index](./html/index.html)