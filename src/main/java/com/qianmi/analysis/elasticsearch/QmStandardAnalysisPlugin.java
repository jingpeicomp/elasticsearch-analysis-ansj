package com.qianmi.analysis.elasticsearch;

import org.ansj.elasticsearch.index.AnsjAnalysisBinderProcessor;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;

public class QmStandardAnalysisPlugin extends AbstractPlugin {

    @Override
    public String name() {
        return "qm_plugin";
    }


    @Override
    public String description() {
        return "qianmi elasticsearch plugin";
    }


    @Override
    public void processModule(Module module) {
        if (module instanceof AnalysisModule) {
            AnalysisModule analysisModule = (AnalysisModule) module;
            analysisModule.addProcessor(new QmStandardAnalysisBinderProcessor());
            analysisModule.addProcessor(new AnsjAnalysisBinderProcessor());
        }
    }

}