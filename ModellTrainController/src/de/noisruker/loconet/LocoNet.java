package de.noisruker.loconet;

import de.noisruker.gui.GuiEditTrain;
import de.noisruker.gui.GuiMain;
import de.noisruker.gui.tables.BasicTrains;
import de.noisruker.loconet.messages.*;
import de.noisruker.main.GUILoader;
import de.noisruker.railroad.Railroad;
import de.noisruker.railroad.Train;
import de.noisruker.railroad.elements.AbstractRailroadElement;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.application.Platform;
import jssc.SerialPortException;
import org.controlsfx.control.Notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LocoNet {

    private static LocoNet instance;
    private static Railroad railroad;

    public static boolean record;
    public static boolean drive;
    public static byte checkConnection = -1;
    public static boolean connectionChecked = false;

    private static final ArrayList<Train> trains = new ArrayList<>();
    private final ArrayList<Sensor> sensors = new ArrayList<>();

    private boolean stopAutoDrive = false;

    public ArrayList<Action> actions = new ArrayList<>();

    private HashMap<Byte, String[]> trainsToAdd = new HashMap<>();

    public static LocoNet getInstance() {
        return instance == null ? instance = new LocoNet() : instance;
    }

    public static Railroad getRailroad() {
        return railroad == null ? railroad = new Railroad() : railroad;
    }

    private LocoNetConnection connection;

    protected LocoNet() {
    }

    public void addSavedTrain(byte address, String name, String picturePath, byte maxSpeed, byte normalSpeed, byte minSpeed, boolean standardDirection) {
        String[] params = new String[]{
                name,
                picturePath,
                Byte.toString(maxSpeed),
                Byte.toString(normalSpeed),
                Byte.toString(minSpeed),
                Boolean.toString(standardDirection)
        };
        trainsToAdd.put(address, params);
        Train.addTrain(address);
    }

    private void addTrain(Train train) {
        trains.add(train);
    }

    public void stopAutoDrive() {
        stopAutoDrive = true;
    }

    public void activateDriveAuto() {
        stopAutoDrive = false;
        new Thread(() -> {
            Railroad r = LocoNet.getRailroad();
            r.init();


            new SwitchMessage((byte) 8, true).send();


            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {

            }


            new SwitchMessage((byte) 8, false).send();


            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {

            }


            new SwitchMessage((byte) 8, true).send();


            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {

            }


            new SwitchMessage((byte) 8, false).send();


            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {

            }

            while (!stopAutoDrive) {
                r.update();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
        }).start();
    }

    public ArrayList<Train> getTrains() {
        return trains;
    }

    public ArrayList<Sensor> getSensors() {
        return this.sensors;
    }

    public void stop() throws SerialPortException {
        LocoNetMessageReceiver.getInstance().removeConnection();
        this.connection.close();
    }

    public void start(String port) throws SerialPortException {
        this.connection = new LocoNetConnection(port);
        this.connection.open();

        LocoNetMessageSender.getInstance().useConnection(this.connection);
        LocoNetMessageReceiver.getInstance().useConnection(this.connection).start();

        LocoNetMessage.registerStandart();

        LocoNetMessageReceiver.getInstance().registerListener(l -> {
            if (l == null)
                return;

            AbstractRailroadElement.sendToElements(l);

//			String params = "[";
//
//			for (byte b : l.toLocoNetMessage().getValues())
//				params += Byte.toString(b) + "|";
//
//			params += "]";
//			Ref.LOGGER.config(
//					"Message Recieved: Type=" + l.toLocoNetMessage().getType().toString() + "; Parameter=" + params);


            if (l instanceof SwitchMessage) {
                Ref.LOGGER.info(l.toString());
            }

            if (l instanceof TrainSlotMessage) {
                if (checkConnection == ((TrainSlotMessage) l).getAddress() || (((TrainSlotMessage) l).getAddress() == 0 && checkConnection != -1)) {
                    connectionChecked = true;
                    checkConnection = -1;
                    return;
                }
                TrainSlotMessage m = ((TrainSlotMessage) l);

                Ref.LOGGER.info("Write requested Train to slot: " + m.getSlot() + " Address: " + m.getAddress() + " Check Connection: " + checkConnection);

                if (m.getAddress() == 0) {
                    Platform.runLater(() -> Notifications.create().darkStyle().title(Ref.language.getString("error.train_not_present"))
                            .text(Ref.language.getString("error.add_train_manual")).owner(GUILoader.getPrimaryStage())
                            .showError());
                    return;
                }

                if (!this.trainExists(m.getAddress())) {
                    LocoNet.getRailroad().stopTrainControlSystem();

                    while (!LocoNet.getRailroad().isControlSystemStopped()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ignored) {
                        }
                    }

                    final Train train;
                    this.addTrain(train = new Train(m.getAddress(), m.getSlot()));

                    Util.runNext(() -> this.handleTrainAppearance(train));
                }

                BasicTrains.getInstance().setTrains(this.getTrains());
            }

            if (l instanceof DirectionMessage) {
//				Ref.LOGGER.info("Set direction to: " + ((DirectionMessage) l).getFunktion());
            }

            if (l instanceof SpeedMessage) {
                for (Train t : LocoNet.trains) {
                    if (t.getSlot() == ((SpeedMessage) l).getSlot()) {
                        if (t.getActualSpeed() != ((SpeedMessage) l).getSpeed() && t.getSpeed() != ((SpeedMessage) l).getSpeed()) {
                            t.setSpeed(((SpeedMessage) l).getSpeed());
                        }
                    }
                }
            }

            if (l instanceof SensorMessage) {
                SensorMessage s = (SensorMessage) l;

                //Ref.LOGGER.fine("Sensor " + s.getAddress() + " changed state to " + s.getState() + ".");
            }
        });

    }

    private void handleTrainAppearance(Train train) {
        if (trainsToAdd.containsKey(train.getAddress())) {
            String[] params = trainsToAdd.get(train.getAddress());
            train.setParameters(params[0], Byte.parseByte(params[2]), Byte.parseByte(params[3]), Byte.parseByte(params[4]), Boolean.parseBoolean(params[5]));
            if (params[1] != null)
                train.setPicturePath(params[1]);
        } else {
            while (GuiEditTrain.train != null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {
                }
            }
            GuiEditTrain.train = train;
            Platform.runLater(() -> Objects.requireNonNull(
                    Util.openWindow("/assets/layouts/edit_train.fxml",
                            Ref.language.getString("window.edit_train"),
                            GUILoader.getPrimaryStage()))
                    .setResizable(true));
        }
        if (GuiMain.getInstance() != null)
            GuiMain.getInstance().updateTrains();
    }

    public boolean trainExists(byte address) {
        for (Train t : trains)
            if (t.getAddress() == address)
                return true;
        return false;
    }

}
