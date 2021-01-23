package de.noisruker.util;

public enum Theme {

    LIGHT("light"), DARK("/de/noisruker/util/theme.css");

    protected String location;

    Theme(String location) {
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }

}
