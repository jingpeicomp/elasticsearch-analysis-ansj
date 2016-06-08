package com.qianmi.analysis.elasticsearch;

import org.elasticsearch.common.inject.AbstractModule;

/**
 * QmStandardAnalysisModule
 * Created by liuzhaoming on 16/2/22.
 */
public class QmStandardAnalysisModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(QmStandardAnalysis.class).asEagerSingleton();
    }
}
