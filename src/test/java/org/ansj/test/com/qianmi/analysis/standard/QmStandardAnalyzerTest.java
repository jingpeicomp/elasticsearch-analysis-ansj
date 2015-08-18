package org.ansj.test.com.qianmi.analysis.standard;

import com.qianmi.analysis.standard.QmStandardAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liuzhaoming on 15/8/3.
 */
public class QmStandardAnalyzerTest {
    private static QmStandardAnalyzer qmAnalyzer;

    @Before
    public void setUp() {
        qmAnalyzer = new QmStandardAnalyzer();
    }

    @After
    public void tearDown() {
        qmAnalyzer = null;
    }

//    @Test
    public void testAnalyzeTokens() {
        String str = "中华人民共和国";
        try {
            List<String> analyzeTokens = getAnalyzeTokens(str);
            String[] expectResult = {"中", "华", "人", "民", "共", "和", "国"};
            Assert.assertArrayEquals(analyzeTokens.toArray(new String[0]), expectResult);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("IOE exception happens");
        }
    }

//    @Test
    public void testAnalyzeTokensNumber() {
        String str = "国abc123";
        try {
            List<String> analyzeTokens = getAnalyzeTokens(str);
            String[] expectResult = {"国", "abc", "123"};
            Assert.assertArrayEquals(analyzeTokens.toArray(new String[0]), expectResult);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("IOE exception happens");
        }
    }

//    @Test
    public void testAnalyzeTokens1() {
        String str = "国abc12, b3";
        try {
            List<String> analyzeTokens = getAnalyzeTokens(str);
            String[] expectResult = {"国", "abc", "12", "b", "3"};
            Assert.assertArrayEquals(analyzeTokens.toArray(new String[0]), expectResult);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("IOE exception happens");
        }
    }

//    @Test
    public void testAnalyzeTokens2() {
        String str = "国abc1ttt**6国32123%%%7777772, b3";
        try {
            List<String> analyzeTokens = getAnalyzeTokens(str);
            String[] expectResult = {"国", "abc", "1", "ttt", "6", "国","32123", "7777772", "b", "3"};
            Assert.assertArrayEquals(analyzeTokens.toArray(new String[0]), expectResult);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("IOE exception happens");
        }
    }

    private List<String> getAnalyzeTokens(String text) throws IOException {
        StringReader reader = new StringReader(text);
        TokenStream ts = qmAnalyzer.tokenStream("content", reader);
        List<String> tokens = new ArrayList<>();
        CharTermAttribute ta = ts.getAttribute(CharTermAttribute.class);
        ts.reset();
        while (ts.incrementToken()) {
            tokens.add(new String(Arrays.copyOf(ta.buffer(), ta.length())));
        }

        return tokens;
    }
}
