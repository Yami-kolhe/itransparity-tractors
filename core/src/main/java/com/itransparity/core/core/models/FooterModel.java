package com.itransparity.core.core.models;

import java.time.Year;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterModel {

    @ValueMapValue private String copyright;

    public String getCopyright() {
        if (copyright != null && !copyright.trim().isEmpty()) {
            return copyright;
        }
        return "\u00A9 " + Year.now().getValue() + " iTransparity. All rights reserved.";
    }
}