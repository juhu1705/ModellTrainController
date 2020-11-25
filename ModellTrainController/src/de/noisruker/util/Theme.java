package de.noisruker.util;

public enum Theme {

    NORMAL("/assets/styles/theme.css"), LIGHT("light"), DARK("/assets/styles/theme.css");

    protected String location;

    Theme(String location) {
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }

}
