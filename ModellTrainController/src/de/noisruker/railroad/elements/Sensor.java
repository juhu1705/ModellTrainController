package de.noisruker.railroad.elements;

import de.noisruker.gui.GuiMain;
import de.noisruker.gui.RailroadImages;
import de.noisruker.loconet.LocoNet;
import de.noisruker.loconet.messages.AbstractMessage;
import de.noisruker.loconet.messages.SensorMessage;
import de.noisruker.railroad.Position;
import de.noisruker.railroad.RailRotation;
import de.noisruker.railroad.Train;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Sensor extends AbstractRailroadElement {

    private static ArrayList<Sensor> allSensors = new ArrayList<>();

    public static ArrayList<Sensor> getAllSensors() {
        return allSensors;
    }

    private static void addSensor(Sensor s) {
        for (int i = 0; i < allSensors.size(); i++) {
            if (allSensors.get(i).address > s.address) {
                allSensors.add(i, s);
                return;
            }
        }
        allSensors.add(s);
    }

    private final int address;

    private boolean state, list;
    private String name = "";

    private Train train;
    private int cooldown = -1, requestCount = 0;

    public Sensor(int address, boolean state, Position position, RailRotation rotation) {
        super("sensor", position, rotation);
        this.address = address;
        this.state = state;
        list = true;
        Sensor.addSensor(this);
    }

    @Override
    public void saveTo(BufferedWriter writer) throws IOException {
        super.saveTo(writer);
        Util.writeParameterToBuffer("address", Integer.toString(this.address));
        if (!name.isBlank())
            Util.writeParameterToBuffer("name", this.name);
        Util.writeParameterToBuffer("list", Boolean.toString(this.list));
        Util.closeWriting();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sensor sensor = (Sensor) o;
        return address == sensor.address;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean shouldBeListed() {
        return this.list;
    }

    public void setShouldBeListed(boolean list) {
        this.list = list;
    }

    public boolean addTrain(Train t) {
        if (this.train == null && (this.equals(t.getActualPosition()) || !this.getState())) {
            Sensor s = LocoNet.getRailroad().getNextSensor(this, t);

            if(s != null && !s.addTrain(t))
                return false;

            this.train = t;
            this.sync();

            return true;
        } else if(this.train != null && this.train.equals(t)) {
            Sensor s = LocoNet.getRailroad().getNextSensor(this, t);
            if(s != null && !s.addTrain(t))
                return false;

            this.sync();

            return true;
        }
        return false;
    }

    public void sync() {
        for (Sensor sensor : Sensor.getAllSensors()) {
            if (this.getAddress() == sensor.getAddress()) {
                if(sensor.requestCount < 0)
                    sensor.requestCount = 0;

                sensor.train = this.train;
                sensor.requestCount++;
                Ref.LOGGER.info("Sensor: " + this.toString() + "; Count: " + requestCount);
                sensor.updateGUI();
            }
        }
    }

    public void updateGUI() {
        if(GuiMain.getInstance() == null)
            return;
        Platform.runLater(() -> {
            GuiMain gui = GuiMain.getInstance();
            HBox box = gui.railroadLines.get(this.position.getY());
            gui.railroadCells.get(box).get(this.position.getX()).setImage(this.getImage());
        });
    }

    public void onTrainLeft(int address) {
        if (address == this.getAddress() && this.train != null) {
            Ref.LOGGER.info("Sensor: " + this.toString() + "; Count: " + requestCount);
            this.cooldown = 4;
        }
        if (this.getAddress() == 2 && address == 2) {
            for (Signal s : Signal.getAllSignals())
                s.getMessage(false).toLocoNetMessage().send();
        }
    }

    public void onTrainEnter(int address) {
        if (address == this.address) {
            Ref.LOGGER.info("Sensor: " + this.toString() + "; Count: " + requestCount);
            if(this.cooldown != -1)
                this.cooldown = -1;
        }
    }

    public void update() {
        if (cooldown > 0)
            cooldown--;
        else if(cooldown == 0) {
            cooldown = -1;
            requestCount--;
            Ref.LOGGER.info("Sensor: " + this.toString() + "; Count: " + requestCount);
            if(requestCount < 0)
                requestCount = 0;
            if(requestCount == 0)
                this.trainLeft();
        }
    }

    public Train trainLeft() {
        Train t = this.train;
        this.train = null;

        if (GuiMain.getInstance() != null)
            this.updateGUI();
        return t;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address);
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getAddress() {
        return this.address;
    }

    public boolean getState() {
        return this.state;
    }

    @Override
    public Image getImage() {
        return rotation.equals(RailRotation.NORTH) || rotation.equals(RailRotation.SOUTH) ?
                state ? RailroadImages.STRAIGHT_SENSOR_VERTICAL_OFF :
                        this.train == null ? RailroadImages.STRAIGHT_SENSOR_VERTICAL :
                                RailroadImages.STRAIGHT_SENSOR_VERTICAL_RESERVED :
                state ? RailroadImages.STRAIGHT_SENSOR_HORIZONTAL_OFF :
                        this.train == null ? RailroadImages.STRAIGHT_SENSOR_HORIZONTAL :
                                RailroadImages.STRAIGHT_SENSOR_HORIZONTAL_RESERVED;
    }

    @Override
    public void onLocoNetMessage(AbstractMessage message) {
        if (message instanceof SensorMessage) {
            SensorMessage m = (SensorMessage) message;
            if (m.getAddress() == this.getAddress()) {
                this.setState(m.getState());
                if (GuiMain.getInstance() != null)
                    Platform.runLater(() -> {
                        GuiMain gui = GuiMain.getInstance();
                        HBox box = gui.railroadLines.get(this.position.getY());
                        gui.railroadCells.get(box).get(this.position.getX()).setImage(this.getImage());
                    });
            }
        }
    }

    @Override
    public Position getToPos(Position from) {
        switch (rotation) {
            case NORTH, SOUTH:
                if (from.equals(new Position(position.getX(), position.getY() + 1)))
                    return new Position(position.getX(), position.getY() - 1);
                else if (from.equals(new Position(position.getX(), position.getY() - 1)))
                    return new Position(position.getX(), position.getY() + 1);
                break;
            case WEST, EAST:
                if (from.equals(new Position(position.getX() - 1, position.getY())))
                    return new Position(position.getX() + 1, position.getY());
                else if (from.equals(new Position(position.getX() + 1, position.getY())))
                    return new Position(position.getX() - 1, position.getY());
                break;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name.isBlank() ? "Sensor: " + this.address + " [" + super.position.getX() + "] [" + super.position.getY() + "]" : this.name;
    }

    public Train getTrain() {
        return this.train;
    }

    public boolean isFree(Train train) {
        Sensor s = LocoNet.getRailroad().getNextSensor(this, train);

        if(s != null && !s.isFree(train))
            return false;

        if (train.equals(this.train)) return true;
        if (this.train != null) return false;
        return !this.getState();
    }

    public void reset() {
        this.train = null;
        this.requestCount = 0;
        this.cooldown = -1;
        this.updateGUI();
    }
}
