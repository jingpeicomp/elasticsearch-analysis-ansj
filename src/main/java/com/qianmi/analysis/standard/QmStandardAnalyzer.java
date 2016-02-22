package com.qianmi.analysis.standard;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.std40.StandardTokenizer40;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;

/**
 * QM标准分词
 * Created by liuzhaoming on 15/8/3.
 */
public final class QmStandardAnalyzer extends StopwordAnalyzerBase {
    /**
     * Default maximum allowed token length
     */
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

    private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;

    /**
     * An unmodifiable set containing some common English words that are usually not
     * useful for searching.
     */
//    public static final CharArraySet STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    public static final CharArraySet STOP_WORDS_SET = CharArraySet.EMPTY_SET;

    /**
     * Builds an analyzer with the given stop words.
     *
     * @param stopWords stop words
     */
    public QmStandardAnalyzer(CharArraySet stopWords) {
        super(stopWords);
    }

    /**
     * Builds an analyzer with the default stop words ({@link #STOP_WORDS_SET}).
     */
    public QmStandardAnalyzer() {
        this(STOP_WORDS_SET);
    }

    /**
     * Builds an analyzer with the stop words from the given reader.
     *
     * @param stopwords Reader to read stop words from
     * @see org.apache.lucene.analysis.util.WordlistLoader#getWordSet(java.io.Reader)
     */
    public QmStandardAnalyzer(Reader stopwords) throws IOException {
        this(loadStopwordSet(stopwords));
    }

    /**
     * Set maximum allowed token length.  If a token is seen
     * that exceeds this length then it is discarded.  This
     * setting only takes effect the next time tokenStream or
     * tokenStream is called.
     */
    public void setMaxTokenLength(int length) {
        maxTokenLength = length;
    }

    /**
     * @see #setMaxTokenLength
     */
    public int getMaxTokenLength() {
        return maxTokenLength;
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        final StandardTokenizer src = new StandardTokenizer();
        src.setMaxTokenLength(maxTokenLength);
        TokenStream tok = new StandardFilter(src);
        tok = new LowerCaseFilter(tok);
        tok = new StopFilter(tok, stopwords);

        return new TokenStreamComponents(src, tok) {
            @Override
            protected void setReader(final Reader reader) {
                src.setMaxTokenLength(QmStandardAnalyzer.this.maxTokenLength);
                super.setReader(reader);
            }

            public TokenStream getTokenStream() {
                LetterCutNumberFilter source = new LetterCutNumberFilter(sink);
                try {
                    source.reset();
                    source.consumeAllTokens();
                    source.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return source.getSink();
            }
        };
    }
}
