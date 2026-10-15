package com.itransparity.core.core.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class StoreFinderModel {


    public static class Region {
        private final String key;
        private final String label;
        private final Double lat;
        private final Double lon;

        Region(String key, String label, Double lat, Double lon) {
            this.key = key;
            this.label = label;
            this.lat = lat;
            this.lon = lon;
        }

        public String getKey()   { return key; }
        public String getLabel() { return label; }
        Double getLat()          { return lat; }
        Double getLon()          { return lon; }
    }

    private static final List<Region> REGIONS = Collections.unmodifiableList(Arrays.asList(
            new Region("london", "London, UK", 51.5074, -0.1278),
            new Region("nairobi", "Nairobi, Kenya", -1.2921, 36.8219),
            new Region("delhi", "Delhi, India", 28.6139, 77.2090),
            new Region("other", "Other / unlisted", null, null)));

    @SlingObject private Resource resource;


    public String getEndpoint() {
        return resource.getPath();
    }

    public List<Region> getRegions() {
        return REGIONS;
    }

    public String getRegionsJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < REGIONS.size(); i++) {
            Region r = REGIONS.get(i);
            if (i > 0) {
                sb.append(',');
            }
            sb.append("{\"key\":\"").append(r.key).append("\",\"label\":\"").append(r.label).append('"');
            if (r.lat != null) {
                sb.append(String.format(Locale.ROOT, ",\"lat\":%s,\"lon\":%s", r.lat, r.lon));
            }
            sb.append('}');
        }
        return sb.append(']').toString();
    }
}