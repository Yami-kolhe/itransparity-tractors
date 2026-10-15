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
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.wcm.api.Page;

@Model(
        adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class TractorDetailModel {

    private static final String FRAGMENTS_ROOT =
            "/content/dam/itransparity/tractor/";

    private static final String DATA_PATH =
            "jcr:content/data/master";

    @ScriptVariable
    private Page currentPage;

    @ScriptVariable
    private ValueMap pageProperties;

    @SlingObject
    private ResourceResolver resolver;

    @ValueMapValue(name = "fragmentPath")
    private String dialogPath;

    private ValueMap data;

    private final List<Spec> specs = new ArrayList<>();

    @PostConstruct
    protected void init() {

        String path = dialogPath;

        if (isBlank(path) && pageProperties != null) {
            path = pageProperties.get("fragmentPath", String.class);
        }

        if (isBlank(path) && currentPage != null) {
            path = FRAGMENTS_ROOT + currentPage.getName();
        }

        if (isBlank(path)) {
            return;
        }

        Resource node = resolver.getResource(path + "/" + DATA_PATH);

        if (node == null) {
            return;
        }

        data = node.getValueMap();

        addSpec("Horsepower", number("horsepower"), " HP");
        addSpec("Engine type", text("engineType"), "");
        addSpec("Weight", number("weight"), " kg");
        addSpec("Fuel capacity", number("fuelCapacity"), " L");
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void addSpec(String label, String value, String unit) {
        if (!value.isEmpty()) {
            specs.add(new Spec(label, value + unit));
        }
    }

    private String text(String key) {
        if (data == null) {
            return "";
        }

        String value = data.get(key, String.class);

        return value == null ? "" : value;
    }

    private String number(String key) {
        String value = text(key);

        return value.endsWith(".0")
                ? value.substring(0, value.length() - 2)
                : value;
    }

    public String getName() {
        return text("modelName");
    }

    public String getImageUrl() {
        return text("image");
    }

    public String getDescription() {
        return text("description");
    }

    public String getCtaLabel() {
        return text("ctaLabel");
    }

    public String getCtaLink() {
        String link = text("ctaLink");

        if (isBlank(link)) {
            return "";
        }

        if (link.startsWith("/content/")
                && !link.endsWith(".html")
                && !link.contains("#")
                && !link.contains("?")) {

            return link + ".html";
        }

        return link;
    }

    public List<Spec> getSpecs() {
        return Collections.unmodifiableList(specs);
    }

    public boolean isEmpty() {
        return data == null;
    }


    public String getBackLink() {

        if (currentPage == null) {
            return "#";
        }

        String path = currentPage.getPath();


        int tractorPage = path.lastIndexOf('/');

        int languagePage = path.lastIndexOf('/', tractorPage - 1);

        return languagePage > 0
                ? path.substring(0, languagePage) + "/home.html"
                : "#";
    }

    public static class Spec {

        private final String label;
        private final String value;

        public Spec(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }
    }
}