package com.qianmi.analysis.elasticsearch;

import com.qianmi.analysis.sub.SubStringAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.settings.IndexSettings;

/**
 * Created by liuzhaoming on 2018/7/23.
 */
public class SubStringAnalyzerProvider extends AbstractIndexAnalyzerProvider<Analyzer> {

    private final Analyzer analyzer;

    @Inject
    public SubStringAnalyzerProvider(Index index, @IndexSettings Settings indexSettings,
                                     Environment env, @Assisted String name,
                                     @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
        String positionStr = settings.get("position");
        analyzer = new SubStringAnalyzer(positionStr);
    }

    public SubStringAnalyzerProvider(Index index, Settings indexSettings, String name,
                                     Settings settings) {
        super(index, indexSettings, name, settings);
        String positionStr = settings.get("position");
        analyzer = new SubStringAnalyzer(positionStr);
    }

    public SubStringAnalyzerProvider(Index index, Settings indexSettings, String prefixSettings,
                                     String name, Settings settings) {
        super(index, indexSettings, prefixSettings, name, settings);
        String positionStr = settings.get("position");
        analyzer = new SubStringAnalyzer(positionStr);
    }

    @Override
    public Analyzer get() {
        return this.analyzer;
    }
}
