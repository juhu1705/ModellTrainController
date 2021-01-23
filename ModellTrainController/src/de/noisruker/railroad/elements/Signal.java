package de.noisruker.railroad.elements;

import de.noisruker.gui.GuiMain;
import de.noisruker.gui.RailroadImages;
import de.noisruker.loconet.messages.AbstractMessage;
import de.noisruker.loconet.messages.SensorMessage;
import de.noisruker.loconet.messages.SwitchMessage;
import de.noisruker.railroad.Position;
import de.noisruker.railroad.RailRotation;
import de.noisruker.util.Util;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Signal extends AbstractRailroadElement {

    private static ArrayList<Signal> allSignals = new ArrayList<>();

    public static ArrayList<Signal> getAllSignals() {
        return allSignals;
    }

    private static void addSignal(Signal s) {
        for (int i = 0; i < allSignals.size(); i++) {
            if (allSignals.get(i).address > s.address) {
                allSignals.add(i, s);
                return;
            }
        }
        allSignals.add(s);
    }

    private boolean state;
    private final byte address;
    private boolean listen = false;

    public Signal(byte address, RailRotation rotation, Position position) {
        super("signal", position, rotation);
        this.address = address;
        Signal.addSignal(this);
    }

    public SwitchMessage getMessage(boolean state) {
        return new SwitchMessage(address, state);
    }

    private void setState(boolean state) {
        this.state = state;
    }

    public void setAndUpdateState(boolean state) {
        this.getMessage(state).toLocoNetMessage().send();
    }

    @Override
    public void saveTo(BufferedWriter writer) throws IOException {
        super.saveTo(writer);
        Util.writeParameterToBuffer("address", Integer.toString(this.address));
        Util.closeWriting();
    }

    @Override
    public Image getImage() {
        switch (rotation) {
            case NORTH:
            case SOUTH:
                if (state)
                    return RailroadImages.SIGNAL_VERTICAL_ON;
                else
                    return RailroadImages.SIGNAL_VERTICAL_OFF;
            case EAST:
            case WEST:
                if (state)
                    return RailroadImages.SIGNAL_HORIZONTAL_ON;
                else
                    return RailroadImages.SIGNAL_HORIZONTAL_OFF;
        }
        return RailroadImages.EMPTY;
    }

    @Override
    public void onLocoNetMessage(AbstractMessage message) {
        if (message instanceof SwitchMessage) {
            SwitchMessage switchMessage = (SwitchMessage) message;
            if (switchMessage.getAddress() == this.address) {
                this.state = switchMessage.getState();
                if (GuiMain.getInstance() != null)
                    Platform.runLater(() -> {
                        GuiMain gui = GuiMain.getInstance();
                        HBox box = gui.railroadLines.get(this.position.getY());
                        gui.railroadCells.get(box).get(this.position.getX()).setImage(this.getImage());
                    });
            }
        } else if (listen && message instanceof SensorMessage) {
            SensorMessage sensorMessage = (SensorMessage) message;
            if (!sensorMessage.getState()) {
                listen = false;
                this.setAndUpdateState(false);
            }
        }
    }

    @Override
    public Position getToPos(Position from) {
        switch (rotation) {
            case NORTH:
            case SOUTH:
                if (from.equals(new Position(position.getX(), position.getY() + 1)))
                    return new Position(position.getX(), position.getY() - 1);
                else if (from.equals(new Position(position.getX(), position.getY() - 1)))
                    return new Position(position.getX(), position.getY() + 1);
                break;
            case WEST:
            case EAST:
                if (from.equals(new Position(position.getX() - 1, position.getY())))
                    return new Position(position.getX() + 1, position.getY());
                else if (from.equals(new Position(position.getX() + 1, position.getY())))
                    return new Position(position.getX() - 1, position.getY());
                break;
        }

        return null;
    }

    public void listen() {
        this.listen = true;
    }

    public void changeState() {
        this.setAndUpdateState(!this.state);
    }

    public byte address() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Signal signal = (Signal) o;
        return address == signal.address;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address);
    }
}
