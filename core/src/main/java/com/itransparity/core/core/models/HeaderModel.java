package com.itransparity.core.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderModel {

    @ScriptVariable private Page currentPage;
    @SlingObject private ResourceResolver resolver;
    @ValueMapValue private String logo;
    @ValueMapValue private String navRoot;

    private Page root;
    private final List<NavItem> items = new ArrayList<>();

    @PostConstruct
    protected void init() {
        PageManager pm = resolver.adaptTo(PageManager.class);
        if (pm != null && navRoot != null && !navRoot.isEmpty()) {
            root = pm.getPage(navRoot);
        }
        if (root == null && currentPage != null) {
            root = currentPage.getAbsoluteParent(3);
        }
        if (root == null) {
            return;
        }
        Iterator<Page> children = root.listChildren();
        while (children.hasNext()) {
            Page child = children.next();
            if (child.isHideInNav()) {
                continue;
            }
            String path = child.getPath();
            boolean active = currentPage != null
                    && (currentPage.getPath().equals(path)
                    || currentPage.getPath().startsWith(path + "/"));
            String title = child.getNavigationTitle() != null
                    ? child.getNavigationTitle() : child.getTitle();
            items.add(new NavItem(title, path + ".html", active));
        }
    }

    public String getLogo()         { return logo; }
    public String getHomeLink()     { return root == null ? "#" : root.getPath() + "/home.html"; }
    public List<NavItem> getItems() { return Collections.unmodifiableList(items); }
}