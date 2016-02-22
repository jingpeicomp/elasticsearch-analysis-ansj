package com.qianmi.analysis.elasticsearch;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.plugins.Plugin;

import java.util.Collection;
import java.util.Collections;

/**
 * 支持ES 2.*.*版本新插件注册方式
 * Created by liuzhaoming on 16/2/19.
 */
public class QmStandardAnalysisPlugin extends Plugin {

    @Override
    public String name() {
        return "qm_analyzer_plugin";
    }


    @Override
    public String description() {
        return "qianmi elasticsearch analyzer plugin";
    }


    @Override
    public Collection<Module> nodeModules() {
        return Collections.<Module>singletonList(new QmStandardAnalysisModule());
    }

}