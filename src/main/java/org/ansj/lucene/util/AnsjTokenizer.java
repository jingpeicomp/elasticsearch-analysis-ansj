package org.ansj.lucene.util;

import org.ansj.domain.Term;
import org.ansj.splitWord.Analysis;
import org.ansj.util.AnsjReader;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

public class AnsjTokenizer extends Tokenizer {
    // 当前词
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    // 偏移量
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    // 距离
    private final PositionIncrementAttribute positionAttr = addAttribute(PositionIncrementAttribute.class);

    private static final Pattern pattern = Pattern.compile("[^\\u4e00-\\u9fa5A-Za-z0-9 ]+");


    protected Analysis ta = null;
    /**
     * 自定义停用词
     */
    private Set<String> filter;

    public AnsjTokenizer(Analysis ta, Set<String> filter) {
        this.ta = ta;
        this.filter = filter;
    }

    public AnsjTokenizer(Analysis ta) {
        this.ta = ta;
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();
        int position = 0;
        Term term;
        String name;
        int length = 0;
        boolean flag = true;
        do {
            term = ta.next();
            if (term == null) {
                break;
            }
            name = term.getName();
            length = name.length();

            if (filter != null && filter.contains(name)) {
            } else if (pattern.matcher(name).matches()) {
            } else {
                position++;
                flag = false;
            }
        } while (flag);
        if (term != null) {
            positionAttr.setPositionIncrement(position);
            termAtt.setEmpty().append(term.getName());
            offsetAtt.setOffset(term.getOffe(), term.getOffe() + length);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 必须重载的方法，否则在批量索引文件时将会导致文件索引失败
     */
    @Override
    public void reset() throws IOException {
        super.reset();
        ta.resetContent(new AnsjReader(this.input));
    }

}