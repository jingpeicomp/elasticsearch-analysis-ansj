package com.qianmi.analysis.elasticsearch;

import com.qianmi.analysis.standard.QmStandardAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.analysis.*;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;

/**
 * 支持ES 2.*.*版本新插件注册方式
 * Created by liuzhaoming on 16/2/19.
 */
public class QmStandardAnalysis extends AbstractComponent {
    @SuppressWarnings("unused")
    @Inject
    public QmStandardAnalysis(Settings settings, IndicesAnalysisService indicesAnalysisService, Environment env) {
        super(settings);

        //初始化qm_standard分词
        final int maxTokenLength = settings.getAsInt("max_token_length", StandardAnalyzer.DEFAULT_MAX_TOKEN_LENGTH);

        indicesAnalysisService.analyzerProviderFactories().put("qm_standard", new PreBuiltAnalyzerProviderFactory
                ("qm_standard", AnalyzerScope.GLOBAL, new QmStandardAnalyzer()));

        indicesAnalysisService.tokenizerFactories().put("qm_standard", new PreBuiltTokenizerFactoryFactory(new TokenizerFactory() {
            @Override
            public String name() {
                return "qm_standard";
            }

            @Override
            public Tokenizer create() {
                StandardTokenizer tokenizer = new StandardTokenizer();
                tokenizer.setMaxTokenLength(maxTokenLength);
                return tokenizer;
            }
        }));

        indicesAnalysisService.tokenFilterFactories().put("qm_standard", new PreBuiltTokenFilterFactoryFactory(new TokenFilterFactory() {
            @Override
            public String name() {
                return "qm_standard";
            }

            @Override
            public TokenStream create(TokenStream tokenStream) {
                return new StandardFilter(tokenStream);
            }
        }));
    }
}
