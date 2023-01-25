package io.github.svaponi.neo4j.analyzers;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.ResultTransformer;
import org.neo4j.graphdb.Transaction;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestUtils {

    static ResultTransformer<List<Map<String, Object>>> transformer = result -> result.stream().collect(Collectors.toList());

    static List<Map<String, Object>> execute(GraphDatabaseService db, String query) {
        return execute(db, query, false);
    }

    static List<Map<String, Object>> executeIgnoreErrors(GraphDatabaseService db, String query) {
        return execute(db, query, true);
    }

    static private List<Map<String, Object>> execute(GraphDatabaseService db, String query, Boolean ignoreError) {
        try (Transaction tx = db.beginTx()) {
            return db.executeTransactionally(query, Collections.emptyMap(), transformer);
        } catch (Exception e) {
            if (ignoreError) {
                return Collections.emptyList();
            }
            throw e;
        }
    }
}
