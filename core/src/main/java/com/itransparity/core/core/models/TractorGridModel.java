package com.itransparity.core.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TractorGridModel {

    private static final String DATA_PATH = "jcr:content/data/master";

    @ValueMapValue private String folderPath;
    @ValueMapValue private String detailsRoot;
    @SlingObject private ResourceResolver resolver;

    private final List<TractorCard> cards = new ArrayList<>();

    @PostConstruct
    protected void init() {
        if (folderPath == null || folderPath.isEmpty()) {
            return;
        }
        Resource folder = resolver.getResource(folderPath);
        if (folder == null) {
            return;
        }
        for (Resource child : folder.getChildren()) {
            Resource data = child.getChild(DATA_PATH);
            if (data == null) {
                continue;
            }
            ValueMap vm = data.getValueMap();
            cards.add(new TractorCard(
                    text(vm, "modelName"),
                    text(vm, "shortDescription"),
                    text(vm, "image"),
                    number(vm, "horsepower"),
                    text(vm, "engineType"),
                    buildLink(text(vm, "slug"))));
        }
    }

    private String buildLink(String slug) {
        if (detailsRoot == null || detailsRoot.isEmpty() || slug.isEmpty()) {
            return "#";
        }
        return detailsRoot + "/" + slug + ".html";
    }

    private static String text(ValueMap vm, String key) {
        String v = vm.get(key, String.class);
        return v == null ? "" : v;
    }

    private static String number(ValueMap vm, String key) {
        String v = text(vm, key);
        return v.endsWith(".0") ? v.substring(0, v.length() - 2) : v;
    }

    public List<TractorCard> getCards() { return Collections.unmodifiableList(cards); }
    public boolean isEmpty()            { return cards.isEmpty(); }
}