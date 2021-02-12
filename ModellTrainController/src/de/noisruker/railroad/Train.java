package de.noisruker.railroad;

import de.noisruker.gui.GuiMain;
import de.noisruker.loconet.LocoNet;
import de.noisruker.loconet.LocoNetConnection;
import de.noisruker.loconet.messages.*;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.railroad.elements.Switch;
import de.noisruker.util.Config;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import jssc.SerialPortException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Train implements Serializable, Comparable<Train> {

    /**
     * This methods requests the slot of a Train from the LocoNet. Before you use this method make sure you
     * {@link LocoNet#start(String) connected your LocoNet}. After this method is called the LocoNet answer with a
     * {@link TrainSlotMessage train slot message}. This method will be automatically processed by the LocoNet and the
     * new train will be added to the {@link LocoNet#getTrains() list of the trains}.
     *
     * @param address The address of the train
     * @throws SerialPortException                    If there appears an error while sending the data to the LocoNet
     * @throws LocoNetConnection.PortNotOpenException If the port is not open
     */
    public static void addTrain(byte address) {
        if (!LocoNet.getInstance().getTrains().contains(address))
            new LocoNetMessage(MessageType.OPC_LOCO_ADR, (byte) 0, address).send();
    }

    /**
     * The universal Serial-Version-UID
     */
    private static final long serialVersionUID = Ref.UNIVERSAL_SERIAL_VERSION_UID;

    /**
     * The unique address of a train.
     */
    private final byte address;

    /**
     * The slot that is given to the train from the LocoNet
     */
    private final byte slot;

    /**
     * The given name of the train
     */
    private String name;

    /**
     * The path to the picture of the train
     */
    private String picture;

    /**
     * Speed parameters
     */
    private byte speed, maxSpeed, normalSpeed, minSpeed, actualSpeed;

    /**
     * Direction parameters
     */
    protected boolean forward, standardForward;

    /**
     * If the train should stop after a complete drive in in the Railroad section
     */
    protected boolean stopNext = false, wait = false;

    /**
     * Parameters for the auto drive progress
     */
    protected Position prev = null;
    protected Sensor actualSensor = null, nextSensor = null, nextNextSensor = null, previousSensor = null, destination = null, stopAdd = null;

    protected Railway railway = null;
    private final TrainStationManager trainStationManager;

    /**
     * Adds a train with the given slot and address. This Method is only called by {@link LocoNet the LocoNet instance}
     * after receiving a {@link TrainSlotMessage Train slot message} from the LocoNet. If you want to add a train please
     * use {@link Train#addTrain(byte) Train.addTrain(byte address)}. Then wait until the train is listed in
     * {@link LocoNet#getTrains() LocoNet's trains list}.
     *
     * @param address The address of the train
     * @param slot    The given slot from the LocoNet to address this train.
     * @implNote A Train was initialized with the followed parameters:
     * <ul>
     * <li>{@link Train#speed The trains speed is 0}</li>
     * <li>{@link Train#maxSpeed The trains maximal speed is 124}</li>
     * <li>{@link Train#normalSpeed The trains normal speed is 90}</li>
     * <li>{@link Train#minSpeed The trains minimal speed is 35}</li>
     * <li>{@link Train#standardForward The trains direction is set to true.} Means the train drives forward.
     * Some trains drives normally "backwards" so you can set this to false to tell the program that the forward direction of your train is reversed.</li>
     * </ul>
     */
    public Train(byte address, byte slot) {
        this(address, slot, (byte) 0);
    }

    private Train(byte address, byte slot, byte speed) {
        this(address, slot, speed, (byte) 124, (byte) 80, (byte) 35, true);
    }

    private Train(byte address, byte slot, byte speed, byte maxSpeed, byte normalSpeed, byte minSpeed,
                  boolean standardForward) {
        if (LocoNet.getInstance().getTrains().contains(address))
            throw new UnsupportedOperationException("You cannot add an existing Train.");

        this.slot = slot;
        this.address = address;
        this.setName("Train " + this.getAddress());
        this.setMaxSpeed(maxSpeed);
        this.setNormalSpeed(normalSpeed);
        this.setMinSpeed(minSpeed);
        this.setStandardForward(standardForward);
        this.setSpeed(speed);
        this.actualSpeed = this.speed;

        this.trainStationManager = new TrainStationManager(this);

        LocoNet.getRailroad().startTrainControlSystem();
    }

    /* Setter Methods */

    /**
     * Sets the standard parameters of a train
     *
     * @param name            The given name for this train
     * @param maxSpeed        The fastest speed this train could drive with
     * @param normalSpeed     The normal speed the train would drive with
     * @param minSpeed        The slowest speed of the train. Used to stop the train slowly
     * @param standardForward The forward direction of the train. Normally this is true as forward direction.
     *                        But some trains have a reversed direction system.
     */
    public void setParameters(String name, byte maxSpeed, byte normalSpeed, byte minSpeed, boolean standardForward) {
        this.setName(name);
        this.setMaxSpeed(maxSpeed);
        this.setNormalSpeed(normalSpeed);
        this.setMinSpeed(minSpeed);
        this.setStandardForward(standardForward);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpeed(byte speed) {
        if (speed > this.maxSpeed || speed < 0)
            return;

        this.speed = speed == 0 ? 0 : (byte) (speed + 1);
        this.trainSpeedChangeListener.forEach(a -> a.onSpeedChanged(this.speed));
    }

    private void setActualSpeed(byte actualSpeed) {
        this.actualSpeed = actualSpeed;

        new SpeedMessage(this.slot, actualSpeed).send();
    }

    public void setNormalSpeed(byte normalSpeed) {
        this.normalSpeed = normalSpeed;
    }

    public void setMinSpeed(byte brakeSpeed) {
        this.minSpeed = brakeSpeed;
    }

    public void setMaxSpeed(byte maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setStandardForward(boolean standardForward) {
        this.standardForward = standardForward;
    }

    public void setDirection(boolean direction) {
        this.forward = this.standardForward() == direction;
        DirectionMessage message = new DirectionMessage(slot, this.forward);
        message.send();
    }

    public void setPicturePath(String path) {
        this.picture = path;
    }

    /* Boolean returns */

    public boolean standardForward() {
        return standardForward;
    }

    /* Getter Methods */

    public String getName() {
        return this.name;
    }

    public byte getMinSpeed() {
        return minSpeed;
    }

    public byte getNormalSpeed() {
        return normalSpeed;
    }

    public byte getMaxSpeed() {
        return maxSpeed;
    }

    public byte getActualSpeed() {
        return actualSpeed;
    }

    public byte getSpeed() {
        return (byte) (this.speed - 1);
    }

    public byte getAddress() {
        return this.address;
    }

    public byte getSlot() {
        return this.slot;
    }

    public String getPicturePath() {
        return this.picture;
    }

    /* Simple speed controls */

    /**
     * Set the speed of the train to his normal speed.
     */
    public void applyNormalSpeed() {
        this.setSpeed(this.normalSpeed);
    }

    /**
     * Set the speed of the train to his maximal speed.
     */
    public void applyMaxSpeed() {
        this.setSpeed(this.maxSpeed);
    }

    /**
     * Set the speed of the train to his minimal speed.
     */
    public void applyBreakSpeed() {
        this.setSpeed(this.minSpeed);
    }

    /**
     * Stops the train. (Sets his speed to 0)
     *
     * @implNote The train will stop slowly, not immediately
     */
    public void stopTrain() {
        this.setSpeed((byte) 0);
    }

    /**
     * Stops the train at the position where he actually is.
     */
    public void stopTrainImmediately() {
        this.setSpeed((byte) 0);
        this.setActualSpeed((byte) 1);
    }

    /* Methods for the automatic driving process */

    HashMap<Sensor, HashMap<Switch, Integer>> waitForSwitch = new HashMap<>();
    HashMap<Switch, Boolean> switchOnDestination = new HashMap<>();

    private void setUpNextSensors(int nodeAddress) {
        if(nodeAddress != this.nextSensor.getAddress())
            return;

        this.previousSensor = this.actualSensor;
        this.actualSensor = this.nextSensor;
        this.nextSensor = this.nextNextSensor;
        this.nextNextSensor = null;
    }

    private void activateSwitches(int nodeAddress) {
        if (this.actualSensor != null &&
                this.actualSensor.getAddress() == nodeAddress &&
                this.waitForSwitch.containsKey(this.actualSensor))
            this.railway.activateSwitches(this.waitForSwitch.remove(this.actualSensor));
    }

    private void setUpWaiting(int nodeAddress) {
        this.stopTrain();
        this.stopAdd = null;
        this.setUpNextSensors(nodeAddress);
        this.activateSwitches(nodeAddress);
    }

    private void onDestinationReached(int nodeAddress) {
        Ref.LOGGER.info("Reached destination");

        this.setUpNextSensors(nodeAddress);
        this.nextSensor = null;
        this.nextNextSensor = null;

        if(this.previousSensor == null || !this.equals(this.previousSensor.getTrain()) || !this.previousSensor.getState() || this.actualSensor.isShort()) {
            this.stopTrain();
            this.clearRailroad();
        } else
            this.stopNext();

        this.switchOnDestination.forEach(Switch::setAndUpdateState);
        this.switchOnDestination.clear();
    }

    private void clearRailroad() {
        if(this.railway != null)
            this.railway.setPositions(this);

        this.actualSensor = this.destination;

        this.previousSensor = null;
        this.destination = null;
        this.railway = null;
        this.nextSensor = null;
        this.nextNextSensor = null;

        this.stopNext = false;
    }

    private void stopNext() {
        this.stopNext = true;
        this.applyBreakSpeed();
        this.actualSensor = this.destination;

        if (this.railway != null)
            this.railway.setPositions(this);

        Ref.LOGGER.info("Train " + this.getAddress() + " stop on station " + this.actualSensor);
    }

    private void handleSensor(int nodeAddress) {
        Ref.LOGGER.info("Handle sensor");

        if(this.stopAdd != null && this.stopAdd.getAddress() == nodeAddress) {
            if(!this.actualSensor.getState() || this.nextSensor.isShort()) {
                this.stopTrainImmediately();
                this.stopAdd = null;
                this.wait = true;

                Ref.LOGGER.info("Train " + this.address + " is now waiting");
                Ref.LOGGER.info(this.actualSensor + "; " + nodeAddress + "; " + this.previousSensor);
            } else
                this.applyBreakSpeed();
        }

        if(this.nextNextSensor != null && !this.nextNextSensor.isFree(this)) {
            this.setUpNextSensors(nodeAddress);
            this.railway.setLastPosToTrain(this, this.actualSensor);
            return;
        }

        Sensor s = null;

        if(this.nextNextSensor != null && this.nextNextSensor.isFree(this))
            s = this.railway.getNextSensor(this.nextNextSensor);

        this.setUpNextSensors(nodeAddress);

        if (s == null) {
            this.railway.setLastPosToTrain(this, this.actualSensor);
            return;
        }

        if(s.appendTrain(this))
            this.stopAdd = this.nextSensor;

        this.waitForSwitch.put(this.nextSensor, this.railway.getSwitches());
        this.railway.setLastPosToTrain(this, this.actualSensor);
        this.nextNextSensor = s;
    }

    private void printTrain() {
        Ref.LOGGER.info(this.toString() + ": " + this.actualSensor + "; " + this.nextNextSensor);
    }

    public void activateSwitches() {
        if (this.actualSensor != null && this.waitForSwitch.containsKey(this.actualSensor))
            this.railway.activateSwitches(this.waitForSwitch.remove(this.actualSensor));
    }

    public void trainEnter(int nodeAddress) {
        if(this.speed == 0 || Config.mode.equals(Config.MODE_MANUAL))
            return;

        /**if (this.stopAdd != null &&
                this.stopAdd.getAddress() == nodeAddress &&
                (this.actualSensor == null || !this.actualSensor.getState())) {
            this.setUpWaiting(nodeAddress);
            return;
        }*/

        Ref.LOGGER.info("Handle Train enter");

        if (this.destination != null &&
                this.destination.getAddress() == nodeAddress &&
                this.destination.isFree(this) &&
                this.destination.equals(this.nextSensor)) {
            this.onDestinationReached(nodeAddress);
        } else if (this.nextSensor != null &&
                this.nextSensor.getAddress() == nodeAddress &&
                this.destination != null &&
                this.railway != null) {
            this.handleSensor(nodeAddress);
            if(!this.wait)
                this.activateSwitches();
        }

        if(this.actualSensor != null) {
            this.updateGuiPosition();
        }
    }

    public void trainLeft(int nodeAddress) {
        if (this.stopAdd != null &&
                this.stopAdd.getAddress() == actualSensor.getAddress() &&
                this.previousSensor != null &&
                this.previousSensor.getAddress() == nodeAddress) {
            Ref.LOGGER.info("Stop add: " + this.stopAdd + "; " + this.actualSensor);

            this.stopTrain();
            this.stopAdd = null;
            this.wait = true;

            Ref.LOGGER.info("Train " + this.address + " is now waiting");
            Ref.LOGGER.info(this.actualSensor + "; " + nodeAddress + "; " + this.previousSensor);
        }

        if (this.previousSensor != null &&
                this.previousSensor.getAddress() == nodeAddress &&
                this.stopNext) {
            this.stopTrain();

            this.clearRailroad();

            Ref.LOGGER.info(this.toString() + "reached destination " + this.actualSensor.toString());
        }
    }

    public void update() {
        if (!Config.mode.equals(Config.MODE_MANUAL))
            this.checkDriving();

        this.updateSpeed();
    }

    public void reset() {
        this.stopTrainImmediately();
        this.resetRailway();

        this.trainStationManager.stations.clear();

        this.clearRailroad();

        this.stopAdd = null;
        Util.resetSensorsContainsTrain(this);
    }

    private void calculateRailway() {
        Ref.LOGGER.info("Calculate railway");

        this.railway = LocoNet.getRailroad().findWay(this.actualSensor, this.destination, this.prev);

        if (railway == null) {
            Ref.LOGGER.warning("No railway was found");
            return;
        }

        boolean startImmediately = this.railway.init(this);

        if (this.destination == null) {
            Ref.LOGGER.warning("Error due to init Railway");
            this.railway = null;
            return;
        }

        this.railway.print();

        Ref.LOGGER.info("Start immediately: " + startImmediately);

        if(startImmediately)
            this.applyNormalSpeed();
    }

    private void checkForDrivingAgain() {
        if(this.nextSensor == null) {
            if(this.actualSensor == null)
                this.resetRailway();

            this.nextSensor = this.railway.getNextSensor(actualSensor);
            this.waitForSwitch.put(this.actualSensor, this.railway.getSwitches());
            this.nextSensor.appendTrain(this);
        }
    }

    public boolean driveAgain(Sensor sensor) {
        if(this.nextSensor == null || this.destination == null || this.railway == null || sensor == null)
            return false;
        Ref.LOGGER.info("Drive Again: " + sensor + " is free (" + this.stopAdd + ") ");

        if(sensor.equals(this.nextNextSensor) && this.nextSensor.equals(this.stopAdd)) {
            this.stopAdd = null;
            this.applyNormalSpeed();
            return true;
        }

        if(this.nextNextSensor == null && !this.nextSensor.equals(this.destination)) {
            this.nextNextSensor = this.railway.getNextSensor(this.nextSensor);
            this.waitForSwitch.put(this.nextSensor, this.railway.getSwitches());
            if(this.nextNextSensor.appendTrain(this))
                this.stopAdd = this.nextSensor;
            else
                this.stopAdd = null;

            Ref.LOGGER.info("Wait next add " + this.stopAdd + " for sensor " + this.nextNextSensor + " to be free!");
        }

        if(sensor.equals(this.nextSensor)) {
            if(this.waitForSwitch.containsKey(this.actualSensor))
                this.railway.activateSwitches(this.waitForSwitch.remove(this.actualSensor));
            this.wait = false;
            this.applyNormalSpeed();
            return true;
        }
        return false;
    }

    private void checkDriving() {
        if (this.railway == null && this.destination != null)
            this.calculateRailway();
        else if (this.railway != null && this.destination != null && this.speed == 0)
            this.checkForDrivingAgain();


        if (this.speed < this.normalSpeed && !this.stopNext &&
                (this.stopAdd == null || !this.stopAdd.equals(this.actualSensor)) &&
                this.destination != null && this.speed != 0 && this.railway != null) {
            this.applyNormalSpeed();
        }

        if(this.wait && this.speed > 0)
            this.stopTrain();

        if (destination == null && !stopNext) {
            this.trainStationManager.update();
            if(this.speed > 0)
                this.stopTrain();
        }
    }

    private void updateSpeed() {
        if (this.speed == 1 && this.actualSpeed != 1)
            this.setActualSpeed((byte) 1);

        if (this.speed > this.actualSpeed) {
            if (this.speed - 10 < this.actualSpeed)
                this.setActualSpeed(this.speed);
            else
                this.setActualSpeed((byte) (this.actualSpeed + 10));
        } else if (this.speed < this.actualSpeed) {
            if (this.speed + 10 > this.actualSpeed)
                this.setActualSpeed(this.speed);
            else
                this.setActualSpeed((byte) (this.actualSpeed - 10));
        }
    }

    @Override
    public String toString() {
        return name + "{" +
                "address=" + address +
                ", slot=" + slot +
                ", minSpeed=" + minSpeed +
                ", normalSpeed=" + normalSpeed +
                ", maxSpeed=" + maxSpeed +
                '}';
    }

    @Override
    public boolean equals(Object train) {
        if (this == train ||
                (train != null && ((train.getClass().equals(Byte.class) && (byte) train == this.address) ||
                        (train.getClass().equals(String.class) && ((String) train).equals(this.name))))) return true;
        if (train == null || getClass() != train.getClass()) return false;
        return ((Train) train).getAddress() == this.getAddress() &&
                ((Train) train).getSlot() == this.getSlot();
    }

    @Override
    public int compareTo(Train train) {
        return this.getAddress() - train.getAddress();
    }

    private ArrayList<TrainSpeedChangeListener> trainSpeedChangeListener = new ArrayList<>();

    public void registerSpeedChangeListener(TrainSpeedChangeListener listener) {
        this.trainSpeedChangeListener.add(listener);
    }

    public void removeSpeedChangeListener(TrainSpeedChangeListener listener) {
        this.trainSpeedChangeListener.remove(listener);
    }

    public void setActualPosition(Sensor s) {
        this.actualSensor = s;
    }

    public void setLastPosition(Position p) {
        this.prev = p;
        Util.resetSensorsContainsTrain(this);
        this.actualSensor.appendTrain(this);
        this.switchOnDestination.forEach(Switch::setAndUpdateState);
        this.switchOnDestination.clear();
    }

    public void updateLastPosition(Position p) {
        this.prev = p;
    }

    public Sensor getActualPosition() {
        return this.actualSensor;
    }

    public Position getPrevPosition() {
        return this.prev;
    }

    public void setDestination(Sensor s) {
        this.destination = s;
    }

    public Sensor getDestination() {
        return this.destination;
    }

    public void updateTrainStationGuis(VBox trainStations) {
        this.trainStationManager.addStationsToGUI(trainStations);
    }

    public void resetRailway() {
        this.railway = null;
    }

    public TrainStationManager getTrainStationManager() {
        return this.trainStationManager;
    }

    public Sensor getLastSensor() {
        return this.previousSensor;
    }

    public interface TrainSpeedChangeListener {
        void onSpeedChanged(byte newSpeed);
    }

    public void saveTo(BufferedWriter writer) throws IOException {
        Util.setWriter(writer, "train");

        Util.writeParameterToBuffer("address", Byte.toString(this.address));

        Util.writeParameterToBuffer("name", this.name);
        Util.writeParameterToBuffer("picture", this.picture == null ? "" : this.picture);

        Util.writeParameterToBuffer("max", Byte.toString(this.maxSpeed));
        Util.writeParameterToBuffer("normal", Byte.toString(this.normalSpeed));
        Util.writeParameterToBuffer("min", Byte.toString(this.minSpeed));

        Util.writeParameterToBuffer("direction", Boolean.toString(this.standardForward));

        Util.closeWriting();
    }

    public void updateGuiPosition() {
        if (GuiMain.getInstance() != null && this.equals(GuiMain.getInstance().actual))
            Platform.runLater(() -> GuiMain.getInstance().actualPosition.setValue(this.actualSensor.toString()));
    }


}
