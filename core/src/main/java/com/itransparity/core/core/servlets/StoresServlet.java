package com.itransparity.core.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;


@Component(service = Servlet.class)
@SlingServletResourceTypes(
        resourceTypes = "itransparity/components/storefinder",
        selectors = "stores",
        extensions = "json",
        methods = HttpConstants.METHOD_GET)
public class StoresServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_STORES_PATH = "/content/dam/itransparity/stores";
    private static final String DATA_PATH = "jcr:content/data/master";

    @Override
    protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse res) throws IOException {
        String[] selectors = req.getRequestPathInfo().getSelectors();
        String region = selectors.length > 1 ? selectors[1].toLowerCase(Locale.ROOT) : "";

        String path = req.getResource().getValueMap().get("storesPath", DEFAULT_STORES_PATH);
        if (path.trim().isEmpty()) {
            path = DEFAULT_STORES_PATH;
        }

        List<String[]> stores = new ArrayList<>();
        Resource folder = req.getResourceResolver().getResource(path);
        if (folder != null && !region.isEmpty()) {
            for (Resource child : folder.getChildren()) {
                Resource data = child.getChild(DATA_PATH);
                if (data == null) {
                    continue;
                }
                ValueMap vm = data.getValueMap();
                if (region.equalsIgnoreCase(vm.get("region", ""))) {
                    stores.add(new String[] {vm.get("storeName", ""), stripTags(vm.get("address", ""))});
                }
            }
        }
        Collections.sort(stores, Comparator.comparing((String[] s) -> s[0]));

        StringBuilder json = new StringBuilder();
        json.append("{\"region\":\"").append(escape(region)).append("\",\"count\":")
                .append(stores.size()).append(",\"stores\":[");
        for (int i = 0; i < stores.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append("{\"name\":\"").append(escape(stores.get(i)[0]))
                    .append("\",\"address\":\"").append(escape(stores.get(i)[1])).append("\"}");
        }
        json.append("]}");

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(json.toString());
    }

    private static String stripTags(String s) {
        return s.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
    }

    private static String escape(String s) {
        StringBuilder out = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':  out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }
}