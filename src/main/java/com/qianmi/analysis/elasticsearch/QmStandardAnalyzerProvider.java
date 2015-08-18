package com.qianmi.analysis.elasticsearch;

import com.qianmi.analysis.standard.QmStandardAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.settings.IndexSettings;


public class QmStandardAnalyzerProvider extends AbstractIndexAnalyzerProvider<Analyzer> {
    private final Analyzer analyzer;

    @Inject
    public QmStandardAnalyzerProvider(Index index, @IndexSettings Settings indexSettings,
                                      Environment env, @Assisted String name,
                                      @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
        analyzer = new QmStandardAnalyzer();
    }

    public QmStandardAnalyzerProvider(Index index, Settings indexSettings, String name,
                                      Settings settings) {
        super(index, indexSettings, name, settings);
        analyzer = new QmStandardAnalyzer();
    }

    public QmStandardAnalyzerProvider(Index index, Settings indexSettings, String prefixSettings,
                                      String name, Settings settings) {
        super(index, indexSettings, prefixSettings, name, settings);
        analyzer = new QmStandardAnalyzer();
    }

    @Override
    public Analyzer get() {
        return this.analyzer;
    }
}
