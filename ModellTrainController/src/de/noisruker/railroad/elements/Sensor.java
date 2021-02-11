package de.noisruker.railroad.elements;

import de.noisruker.gui.GuiMain;
import de.noisruker.gui.RailroadImages;
import de.noisruker.loconet.LocoNet;
import de.noisruker.loconet.messages.AbstractMessage;
import de.noisruker.loconet.messages.RailroadOffMessage;
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
import java.util.HashMap;
import java.util.Objects;

public class Sensor extends AbstractRailroadElement {

    private static ArrayList<Sensor> allSensors = new ArrayList<>();

    public static ArrayList<Sensor> getAllSensors() {
        return allSensors;
    }

    public static final ArrayList<Integer> UPDATED = new ArrayList<>(), MESSAGE_HANDLED = new ArrayList<>();
    public static final HashMap<Integer, ArrayList<Train>> REQUESTERS = new HashMap<>();

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

    private boolean state, list, isShort;
    private String name = "";

    private Train train;
    private int cooldown = -1;

    public Sensor(int address, boolean state, Position position, RailRotation rotation) {
        super("sensor", position, rotation);
        this.address = address;
        this.state = state;
        this.list = true;
        this.isShort = false;
        Sensor.addSensor(this);
        if(!Sensor.REQUESTERS.containsKey(this.address)) {
            Sensor.REQUESTERS.put(this.address, new ArrayList<>());
        }
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

    public boolean isShort() {
        return this.isShort;
    }

    public void setShort(boolean isShort) {
        this.isShort = isShort;
    }

    public void appendTrain(Train t) {
        if (Sensor.REQUESTERS.containsKey(t) || t.equals(this.train)) {
            if ((this.equals(t.getActualPosition()) && !t.equals(this.train)) || (t.equals(train) && this.state && this.equals(train.getLastSensor())))
                Sensor.REQUESTERS.get(this.address).add(t);
        } else
            Sensor.REQUESTERS.get(this.address).add(t);
    }

    private boolean addTrain(Train t) {
        this.recheckTrains(t);

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
                sensor.train = this.train;
                sensor.cooldown = this.cooldown;
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
        if(Sensor.MESSAGE_HANDLED.contains(this.address))
            return;

        Sensor.MESSAGE_HANDLED.add(this.address);

        if (address == this.getAddress() && this.train != null) {
            this.cooldown = 4;
        }

        if (this.getAddress() == 2 && address == 2) {
            for (Signal s : Signal.getAllSignals())
                s.getMessage(false).toLocoNetMessage().send();
        }

        this.sync();
    }

    public void onTrainEnter(int address) {
        if(Sensor.MESSAGE_HANDLED.contains(this.address))
            return;
        Sensor.MESSAGE_HANDLED.add(this.address);
        if (address == this.address) {
            if(this.cooldown != -1)
                this.cooldown = -1;
        }

        this.sync();
    }

    public void update() {
        if(Sensor.UPDATED.contains(this.address))
            return;

        Sensor.UPDATED.add(this.address);

        if (cooldown > 0 && this.train != null && !this.train.getActualPosition().equals(this))
            cooldown--;
        else if(cooldown > 0 && this.train == null)
            cooldown = -1;
        else if(cooldown == 0) {
            cooldown = -1;
            this.trainLeft();
        }

        if(this.train == null && !this.state && !Sensor.REQUESTERS.get(this.address).isEmpty()) {
            if(this.addTrain(Sensor.REQUESTERS.get(this.address).get(0))) {
                this.train.driveAgain(this);
                Sensor.REQUESTERS.get(this.address).remove(0);
            }
        }

        this.sync();
    }

    public void trainLeft() {
        this.train = null;
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
                this.updateGUI();
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

    private void recheckTrains(Train... except) {
        boolean foundATrain = false;
        for(Train t: LocoNet.getInstance().getTrains()) {
            if(Util.contained(t, except))
                continue;
            if(this.equals(t.getActualPosition()) && this.train == null) {
                this.train = t;

                foundATrain = true;
                this.sync();
            } else if(this.equals(t.getActualPosition()) && !t.equals(this.train)) {
                if(foundATrain || this.train != null) {
                    new RailroadOffMessage().send();
                } else {
                    this.train = t;
                    foundATrain = true;
                    this.sync();
                }
            }
        }
    }

    public boolean isFree(Train train) {
        this.recheckTrains(train);

        Sensor s = LocoNet.getRailroad().getNextSensor(this, train);

        if(s != null && !s.isFree(train))
            return false;

        if (this.train != null) return this.train.equals(train);
        return false;
    }

    public void reset() {
        this.train = null;
        this.cooldown = -1;
        this.updateGUI();
    }
}
