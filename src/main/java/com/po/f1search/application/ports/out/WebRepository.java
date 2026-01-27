package com.po.f1search.application.ports.out;

import com.po.f1search.model.RobotsRules.RobotsRules;
import com.po.f1search.model.utils.Url;
import com.po.f1search.model.WebResource.WebResource;

public interface WebRepository {
    WebResource fetchPage(Url url);
    RobotsRules fetchRobotsRules(Url url);
}
