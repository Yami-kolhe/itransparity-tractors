package com.itransparity.core.core.models;

public class TractorCard {
    private final String name;
    private final String shortDescription;
    private final String imageUrl;
    private final String horsepower;
    private final String engineType;
    private final String link;

    public TractorCard(String name, String shortDescription, String imageUrl,
                       String horsepower, String engineType, String link) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
        this.horsepower = horsepower;
        this.engineType = engineType;
        this.link = link;
    }

    public String getName()             { return name; }
    public String getShortDescription() { return shortDescription; }
    public String getImageUrl()         { return imageUrl; }
    public String getHorsepower()       { return horsepower; }
    public String getEngineType()       { return engineType; }
    public String getLink()             { return link; }
}