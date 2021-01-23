package de.noisruker.util;

public enum Language {

    GERMAN(Ref.getFileString("/assets/language/de.properties")), ENGLISH(Ref.getFileString("/assets/language/en.properties"));

    protected String location;

    Language(String location) {
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }

}
