package io.github.svaponi.neo4j.analyzers;

import com.google.auto.service.AutoService;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.neo4j.graphdb.schema.AnalyzerProvider;

import java.io.IOException;

@AutoService(AnalyzerProvider.class)
public class WhitespaceLowerAnalyzerProvider extends AnalyzerProvider {

    public static final String DESCRIPTION = "Same as \"whitespace\" analyzer, but additionally applies a lower case filter to all tokens.";
    public static final String ANALYZER_NAME = "whitespace_lower";

    public WhitespaceLowerAnalyzerProvider() {
        super(ANALYZER_NAME);
    }

    @Override
    public Analyzer createAnalyzer() {
        try {
            return CustomAnalyzer.builder()
                    .withTokenizer(WhitespaceTokenizerFactory.class, "rule", "java")
                    .addTokenFilter(LowerCaseFilterFactory.class)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }
}
