package com.itransparity.core.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeroModel {

    @ValueMapValue private String headline;
    @ValueMapValue private String subText;
    @ValueMapValue private String ctaLabel;
    @ValueMapValue private String ctaLink;
    @ValueMapValue private String fileReference;

    public String getHeadline() { return headline; }
    public String getSubText()  { return subText; }
    public String getCtaLabel() { return ctaLabel; }
    public String getImageUrl() { return fileReference; }

    public String getCtaLink() {
        if (ctaLink != null && ctaLink.startsWith("/content/") && !ctaLink.contains(".")) {
            return ctaLink + ".html";
        }
        return ctaLink;
    }

    public boolean isEmpty() {
        return isBlank(headline) && isBlank(subText) && isBlank(fileReference);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}