package com.po.f1search.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeneralConfig {

    @Value("${f1search.general.crawlOnBootstrap:false}")
    private boolean _crawlOnBootstrap;

    public boolean isCrawlOnBootstrap() {
        return this._crawlOnBootstrap;
    }
}
