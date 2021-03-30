module ModellTrainController {
    requires java.base;
    requires org.jfxtras.styles.jmetro;
    requires org.controlsfx.controls;
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.logging;
    requires java.desktop;
    requires java.se;
    requires java.xml;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fontawesome;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires maven.model;
    requires plexus.utils;
    requires jssc;
    requires com.google.common;


    opens de.noisruker.gui to javafx.fxml;
    opens de.noisruker.main to javafx.fxml;
    opens assets.language to javafx.base;
    opens assets.layouts to javafx.fxml;

    exports de.noisruker.main;
    exports de.noisruker.gui;
}