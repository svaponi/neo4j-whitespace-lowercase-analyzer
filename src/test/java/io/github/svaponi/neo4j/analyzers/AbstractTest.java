package io.github.svaponi.neo4j.analyzers;

import org.junit.jupiter.api.BeforeAll;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Objects;

abstract class AbstractTest {

    protected static Neo4j neo4j;
    protected static GraphDatabaseService db;

    @BeforeAll
    static void initializeNeo4j() throws IOException {
        var sw = new StringWriter();
        try (var in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(AbstractTest.class.getResourceAsStream("/contracts.cypher"))))) {
            in.transferTo(sw);
            sw.flush();
        }
        neo4j = Neo4jBuilders.newInProcessBuilder()
                .withFixture(sw.toString())
                .build();
        db = neo4j.defaultDatabaseService();
    }
}
