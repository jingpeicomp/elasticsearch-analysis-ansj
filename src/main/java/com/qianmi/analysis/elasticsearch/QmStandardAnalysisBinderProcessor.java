package com.qianmi.analysis.elasticsearch;

import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.index.analysis.StandardTokenFilterFactory;
import org.elasticsearch.index.analysis.StandardTokenizerFactory;

public class QmStandardAnalysisBinderProcessor extends AnalysisModule.AnalysisBinderProcessor {
    @Override
    public void processTokenFilters(AnalysisModule.AnalysisBinderProcessor.TokenFiltersBindings tokenFiltersBindings) {
        tokenFiltersBindings.processTokenFilter("qm_standard", StandardTokenFilterFactory.class);
        super.processTokenFilters(tokenFiltersBindings);
    }

    @Override
    public void processAnalyzers(AnalyzersBindings analyzersBindings) {
        analyzersBindings.processAnalyzer("qm_standard", QmStandardAnalyzerProvider.class);
        analyzersBindings.processAnalyzer("default", QmStandardAnalyzerProvider.class);
        super.processAnalyzers(analyzersBindings);
    }

    @Override
    public void processTokenizers(TokenizersBindings tokenizersBindings) {
        tokenizersBindings.processTokenizer("qm_standard", StandardTokenizerFactory.class);
        super.processTokenizers(tokenizersBindings);
    }

}
