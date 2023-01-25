package io.github.svaponi.neo4j.analyzers;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WhitespaceLowerTest extends AbstractTest {

    @Test
    public void checkAnalyzerIsAvailable() {
        List<Map<String, Object>> results = TestUtils.execute(db, "CALL db.index.fulltext.listAvailableAnalyzers() YIELD analyzer, description RETURN analyzer, description");
        Optional<Map<String, Object>> result = results.stream().filter(e -> e.get("analyzer").equals(UnicodeWhitespaceLowerAnalyzerProvider.ANALYZER_NAME)).findAny();
        assertThat(result).isNotEmpty();
        assertThat(result.get().get("analyzer")).isEqualTo(UnicodeWhitespaceLowerAnalyzerProvider.ANALYZER_NAME);
        assertThat(result.get().get("description")).isEqualTo(UnicodeWhitespaceLowerAnalyzerProvider.DESCRIPTION);
    }

    @Test
    public void checkSearchResults() throws Exception {
        TestUtils.executeIgnoreErrors(db, "CALL db.index.fulltext.drop('contract_search')");
        TestUtils.execute(db, "CALL db.index.fulltext.createNodeIndex('contract_search', ['contract'], ['id','contractNumber'], {analyzer: 'whitespace_lower'})");

        // wait for the index to populate
        boolean ready;
        do {
            Thread.sleep(1000);
            List<Map<String, Object>> results = TestUtils.execute(db, "CALL db.indexes() yield name, type, state");
            Object state = results.stream().filter(e -> e.get("name").equals("contract_search")).findAny().map(e -> e.get("state")).orElse("");
            ready = state.equals("ONLINE");
        } while (!ready);

        {
            List<Object> ids = TestUtils.execute(db, "CALL db.index.fulltext.queryNodes('contract_search', '*g100*') YIELD node RETURN node.id AS id").stream().map(e -> e.get("id")).collect(Collectors.toList());
            assertThat(ids).containsExactlyInAnyOrder("0084.11528.G100.110.1", "0078.11523.G100.106.2");
        }
        {
            List<Object> ids = TestUtils.execute(db, "CALL db.index.fulltext.queryNodes('contract_search', '*G100*') YIELD node RETURN node.id AS id").stream().map(e -> e.get("id")).collect(Collectors.toList());
            assertThat(ids).containsExactlyInAnyOrder("0084.11528.G100.110.1", "0078.11523.G100.106.2");
        }
        {
            List<Object> ids = TestUtils.execute(db, "CALL db.index.fulltext.queryNodes('contract_search', '0084.11528.G100*') YIELD node RETURN node.id AS id").stream().map(e -> e.get("id")).collect(Collectors.toList());
            assertThat(ids).containsExactlyInAnyOrder("0084.11528.G100.110.1");
        }
        {
            List<Object> ids = TestUtils.execute(db, "CALL db.index.fulltext.queryNodes('contract_search', '0064.11511*') YIELD node RETURN node.id AS id").stream().map(e -> e.get("id")).collect(Collectors.toList());
            assertThat(ids).containsExactlyInAnyOrder("0064.11511.100.115.1", "0064.11511.100.114.1", "0064.11511.100.112.1");
        }
        {
            List<Object> ids = TestUtils.execute(db, "CALL db.index.fulltext.queryNodes('contract_search', '0064.11511* AND *112*') YIELD node RETURN node.id AS id").stream().map(e -> e.get("id")).collect(Collectors.toList());
            assertThat(ids).containsExactlyInAnyOrder("0064.11511.100.112.1");
        }
        {
            List<Object> ids = TestUtils.execute(db, "CALL db.index.fulltext.queryNodes('contract_search', '0064.11511.100.112.1') YIELD node RETURN node.id AS id").stream().map(e -> e.get("id")).collect(Collectors.toList());
            assertThat(ids).containsExactlyInAnyOrder("0064.11511.100.112.1");
        }
        {
            List<Object> ids = TestUtils.execute(db, "CALL db.index.fulltext.queryNodes('contract_search', '0064.11511.100.110*') YIELD node RETURN node.id AS id").stream().map(e -> e.get("id")).collect(Collectors.toList());
            assertThat(ids).isEmpty();
        }
    }
}
