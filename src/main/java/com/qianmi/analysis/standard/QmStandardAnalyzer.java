package com.qianmi.analysis.standard;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.sinks.TeeSinkTokenFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;

/**
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
    public static final CharArraySet STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;

    /**
     * Builds an analyzer with the given stop words.
     *
     * @param stopWords stop words
     */
    public QmStandardAnalyzer(CharArraySet stopWords) {
        super(stopWords);
    }

    /**
     * @deprecated Use {@link #QmStandardAnalyzer(CharArraySet)}
     */
    @Deprecated
    public QmStandardAnalyzer(Version matchVersion, CharArraySet stopWords) {
        super(matchVersion, stopWords);
    }

    /**
     * Builds an analyzer with the default stop words ({@link #STOP_WORDS_SET}).
     */
    public QmStandardAnalyzer() {
        this(STOP_WORDS_SET);
    }

    /**
     * @deprecated Use {@link #QmStandardAnalyzer()}
     */
    @Deprecated
    public QmStandardAnalyzer(Version matchVersion) {
        this(matchVersion, STOP_WORDS_SET);
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
     * @deprecated Use {@link #QmStandardAnalyzer()}
     */
    @Deprecated
    public QmStandardAnalyzer(Version matchVersion, Reader stopwords) throws IOException {
        this(matchVersion, loadStopwordSet(stopwords, matchVersion));
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
    protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
        final StandardTokenizer src = new StandardTokenizer(getVersion(), reader);
        src.setMaxTokenLength(maxTokenLength);
        TokenStream tok = new StandardFilter(getVersion(), src);
        tok = new LowerCaseFilter(getVersion(), tok);
        tok = new StopFilter(getVersion(), tok, stopwords);
//        tok = new LetterCutNumberFilter(tok).getSink();
        LetterCutNumberFilter source = new LetterCutNumberFilter(tok);
        tok = source.getSink();
//        TeeSinkTokenFilter source = new TeeSinkTokenFilter(tok);
//        tok = source.newSinkTokenStream();
        try {
            source.reset();
            source.consumeAllTokens();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TokenStreamComponents(src, tok) {
            @Override
            protected void setReader(final Reader reader) throws IOException {
                src.setMaxTokenLength(QmStandardAnalyzer.this.maxTokenLength);
                super.setReader(reader);
            }
        };
    }
}
