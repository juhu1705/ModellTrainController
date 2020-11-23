package de.noisruker.gui;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;

public class Progress {
    private static Progress instance;

    public static Progress getInstance() {
        return instance == null ? instance = new Progress() : instance;
    }

    private Label progressDescription;

    public final void bindLabel(Label l) {
        progressDescription = l;
    }

    public final String getText() {
        return progressDescription.getText();
    }

    public final void setProgressDescription(final String text) {
        Platform.runLater(() -> progressDescription.setText(text));
    }

    private DoubleProperty progress;
    private double to;
    private static boolean started = false;

    public final double getProgress() {
        return progress == null ? 0 : progress.get();
    }

    public final void setProgress(double progress) {
        to = progress;
        if(!started) {
            new Thread(() -> {
                started = true;
                double to = Progress.getInstance().to;
                while (to > Progress.getInstance().progress.get()) {
                    Platform.runLater(() -> Progress.getInstance().progress.set(Progress.getInstance().progress.get() + 0.01));
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ignored) { }
                }
                if (to < Progress.getInstance().progress.get())
                    Platform.runLater(() -> Progress.getInstance().progress.set(to));

                started = false;
            }).start();
        }
    }

    public final DoubleProperty progressProperty() {
        return this.progress == null ? this.progress = new SimpleDoubleProperty(0) : this.progress;
    }
}
