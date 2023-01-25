# neo4j-whitespace-lowercase-analyzer

### Quick start

Clone the repository.

```shell
git clone git@github.com:svaponi/neo4j-whitespace-lowercase-analyzer.git
cd neo4j-whitespace-lowercase-analyzer
```

Build.

```shell
mvn clean package
```

Copy the jar into your Neo4j plugins folder.

```shell
cp ./target/neo4j-whitespace-lowercase-analyzer-*.jar $NEO4J_HOME/plugins/
```

### Troubleshooting

If build fails with `java.lang.UnsatisfiedLinkError: Can't load library: /your/path`, you may want to build without
running the tests.

```shell
mvn clean package -DskipTests
```