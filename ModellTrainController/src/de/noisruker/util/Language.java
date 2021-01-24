package de.noisruker.util;

public enum Language {

    GERMAN("/assets/language/de.properties"), ENGLISH("/assets/language/en.properties");

    protected String location;

    Language(String location) {
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }

}
