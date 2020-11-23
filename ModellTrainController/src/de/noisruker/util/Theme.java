package de.noisruker.util;

public enum Theme {

    LIGHT("remove"), DARK("/assets/styles/dark_theme.css");

    protected String location;

    Theme(String location) {
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }

}
