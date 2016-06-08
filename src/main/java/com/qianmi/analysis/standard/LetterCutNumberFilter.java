package com.qianmi.analysis.standard;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by liuzhaoming on 15/7/31.
 * 1. 分离英文和数字，主要是针对商品存在型号的特殊情况，a8250 切成（a, 8250）
 * 2. 分离特殊字符，比如q.boys q_boys 分词为q  boys
 */
public final class LetterCutNumberFilter extends TokenFilter {
    private static final int LETTER = 1;
    private static final int NUMBER = 0;
    // this filters uses attribute type
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);


    private SinkTokenStream sink = null;


    /**
     * Instantiates a new TeeSinkTokenFilter.
     */
    public LetterCutNumberFilter(TokenStream input) {
        super(input);
        sink = newSinkTokenStream();
    }

    public void consumeAllTokens() throws IOException {
        while (incrementToken()) {
        }
    }


    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }

        final char[] buffer = Arrays.copyOf(termAtt.buffer(), termAtt.length());
        State state = captureState();

        List<Character> numberBuffer = new ArrayList<>();
        List<Character> letterBuffer = new ArrayList<>();
        List<CharTermAttributeImpl> attributeList = new ArrayList<>();
        for (char character : buffer) {
            if (isLetter(character)) {
                letterBuffer.add(character);
                int numberBufferLength = numberBuffer.size();
                if (numberBufferLength > 0) {
                    addTermAttributes(attributeList, numberBufferLength, numberBuffer);
                }
            } else if (isNumber(character)) {
                numberBuffer.add(character);
                int letterBufferLength = letterBuffer.size();
                if (letterBufferLength > 0) {
                    addTermAttributes(attributeList, letterBufferLength, letterBuffer);
                }
            } else if (isSegmentSign(character)) {
                //表示是需要忽略的英文符号
                int numberBufferLength = numberBuffer.size();
                if (numberBufferLength > 0) {
                    addTermAttributes(attributeList, numberBufferLength, numberBuffer);
                }

                int letterBufferLength = letterBuffer.size();
                if (letterBufferLength > 0) {
                    addTermAttributes(attributeList, letterBufferLength, letterBuffer);
                }
            }
        }
        addTermAttributes(attributeList, numberBuffer.size(), numberBuffer);
        addTermAttributes(attributeList, letterBuffer.size(), letterBuffer);
        addStates(state, attributeList);

        return true;
    }

    public SinkTokenStream getSink() {
        return sink;
    }


    @Override
    public final void end() throws IOException {
        super.end();
        AttributeSource.State finalState = captureState();
        sink.setFinalState(finalState);
    }

    private CharSequence getCharSequence(List<Character> charBuffer) {
        StringBuilder sb = new StringBuilder();
        for (Character curChar : charBuffer) {
            sb.append(curChar);
        }
        return sb;
    }


    /**
     * 字符是否是英文字符
     *
     * @param curChar
     * @return
     */
    private boolean isLetter(char curChar) {
        return (curChar >= 'a' && curChar <= 'z') || (curChar >= 'A' && curChar <= 'Z');
    }

    /**
     * 字符是否是数字
     */
    private boolean isNumber(char curChar) {
        return curChar >= '0' && curChar <= '9';
    }

    private boolean isSegmentSign(char curChar) {
        return curChar == '.' || curChar == '_' || curChar == '\'' || curChar == ':';
    }

    private SinkTokenStream newSinkTokenStream() {
        SinkTokenStream sink = new SinkTokenStream(this.cloneAttributes());
        return sink;
    }

    private void addTermAttributes(List<CharTermAttributeImpl> attributeList, int bufferLength, List<Character>
            charBuffer) {
        if (bufferLength == 0) {
            return;
        }
        PackedTokenAttributeImpl newTermAttr = new PackedTokenAttributeImpl();
        newTermAttr.append(getCharSequence(charBuffer));
        newTermAttr.setLength(bufferLength);
        attributeList.add(newTermAttr);
        charBuffer.clear();
    }

    private void addStates(State state, List<CharTermAttributeImpl> attributeList) {
        if (attributeList.size() <= 1) {
            sink.addState(state);
        } else {
            for (int i = 0, size = attributeList.size(); i < size; i++) {
                State curState;
                if (i == 0) {
                    curState = state;
                    StateUtility.setAttribute(attributeList.get(i), curState);
                } else {
                    curState = StateUtility.createState(attributeList.get(i), null);
                }
                sink.addState(curState);
            }

        }
    }

    /**
     * TokenStream output from a tee with optional filtering.
     */
    public static final class SinkTokenStream extends TokenStream {
        private final List<AttributeSource.State> cachedStates = new LinkedList<>();
        private AttributeSource.State finalState;
        private Iterator<AttributeSource.State> it = null;

        private SinkTokenStream(AttributeSource source) {
            super(source);
            addAttribute(CharTermAttribute.class);
        }


        private void addState(AttributeSource.State state) {
            if (it != null) {
                throw new IllegalStateException("The tee must be consumed before sinks are consumed.");
            }
            cachedStates.add(state);
        }

        private void setFinalState(AttributeSource.State finalState) {
            this.finalState = finalState;
        }

        @Override
        public final boolean incrementToken() {
            // lazy init the iterator
            if (it == null) {
                it = cachedStates.iterator();
            }

            if (!it.hasNext()) {
                return false;
            }

            AttributeSource.State state = it.next();
            restoreState(state);
            return true;
        }

        @Override
        public final void end() {
            if (finalState != null) {
                restoreState(finalState);
            }
        }

        @Override
        public final void reset() {
            it = cachedStates.iterator();
        }
    }

    private static final class StateUtility {
        public static State createState(AttributeImpl attribute, State nextState) {
            State state = new State();
            try {
                if (null != attribute) {
                    Field attributeField = state.getClass().getDeclaredField("attribute");
                    attributeField.setAccessible(true);
                    attributeField.set(state, attribute);
                }

                if (null != nextState) {
                    Field nextField = state.getClass().getDeclaredField("next");
                    nextField.setAccessible(true);
                    nextField.set(state, nextState);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException("Fail to create state by reflect.");
            }
            return state;
        }

        public static State setAttribute(AttributeImpl attribute, State state) {
            try {
                Field attributeField = state.getClass().getDeclaredField("attribute");
                attributeField.setAccessible(true);
                attributeField.set(state, attribute);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException("Fail to set attribute in state by reflect.");
            }
            return state;
        }

        public static State setNext(State nextState, State state) {
            try {
                Field nextField = state.getClass().getDeclaredField("next");
                nextField.setAccessible(true);
                nextField.set(state, nextState);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException("Fail to set next in state by reflect.");
            }
            return state;
        }

        public static State getNext(State state) {
            try {
                Field nextField = state.getClass().getDeclaredField("next");
                nextField.setAccessible(true);
                return (State) nextField.get(state);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException("Fail to set next in state by reflect.");
            }
        }
    }
}

