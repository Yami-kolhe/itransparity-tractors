package com.itransparity.core.core.models;

public class NavItem {
    private final String title;
    private final String link;
    private final boolean active;

    public NavItem(String title, String link, boolean active) {
        this.title = title;
        this.link = link;
        this.active = active;
    }

    public String getTitle()  { return title; }
    public String getLink()   { return link; }
    public boolean isActive() { return active; }
}