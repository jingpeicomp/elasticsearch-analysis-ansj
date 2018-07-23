package com.qianmi.analysis.sub;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

import java.io.Reader;

/**
 * Created by liuzhaoming on 2018/7/23.
 */
public class SubStringAnalyzer extends Analyzer {
    /**
     * Creates a new {@link TokenStreamComponents} instance for this analyzer.
     *
     * @param fieldName the name of the fields content passed to the
     *                  {@link TokenStreamComponents} sink as a reader
     * @param reader    the reader passed to the {@link Tokenizer} constructor
     * @return the {@link TokenStreamComponents} for this analyzer.
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer subStringTokenizer = new SubStringTokenizer(reader);
        return new TokenStreamComponents(subStringTokenizer);
    }
}
