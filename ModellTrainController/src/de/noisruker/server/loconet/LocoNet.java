package de.noisruker.server.loconet;

import java.io.IOException;
import java.util.ArrayList;

import de.noisruker.common.Sensor;
import de.noisruker.common.Switch;
import de.noisruker.common.SwitchWay;
import de.noisruker.common.Track;
import de.noisruker.common.Train;
import de.noisruker.common.messages.SensorMessage;
import de.noisruker.common.messages.TrainSlotMessage;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketType;
import de.noisruker.server.ClientHandler;
import de.noisruker.server.Server;
import de.noisruker.server.gui.GUIServer;
import de.noisruker.server.loconet.messages.LocoNetMessage;
import de.noisruker.util.Ref;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import jssc.SerialPortException;

public class LocoNet {

	private static LocoNet instance;

	private ArrayList<Train> trains = new ArrayList<>();
	private ArrayList<Track> tracks = new ArrayList<>();

	public static LocoNet getInstance() {
		return instance == null ? instance = new LocoNet() : instance;
	}

	private LocoNetConnection connection;

	protected LocoNet() {
	}

	private void addTrain(Train train) {
		this.trains.add(train);
	}

	public void start(String port) throws SerialPortException {
		this.connection = new LocoNetConnection(port);
		this.connection.open();

		LocoNetMessageSender.getInstance().useConnection(this.connection);
		LocoNetMessageReciever.getInstance().useConnection(this.connection).start();

		LocoNetMessage.registerStandart();

		Track t0, t1, t2, t3;

		Switch s1, s2;

		this.tracks.add(t0 = new Track("t0", new Sensor((byte) 0, (byte) 64)));
		this.tracks.add(t1 = new Track("t1", new Sensor((byte) 0, (byte) 96)));
		this.tracks.add(t2 = new Track("t2", new Sensor((byte) 1, (byte) 96)));
		this.tracks.add(t3 = new Track("t3", new Sensor((byte) 1, (byte) 64), s1 = new Switch((byte) 0),
				s2 = new Switch((byte) 2)));

		t0.addSwitchWay(new SwitchWay(t2, t3));
		t1.addSwitchWay(new SwitchWay(t3, t3));
		t2.addSwitchWay(new SwitchWay(t3, t0));

		t3.addSwitchWays(new SwitchWay(t2, t0).addSwitch(s2, true).addSwitch(s1, false),
				new SwitchWay(t1, t0).addSwitch(s2, !false).addSwitch(s1, !false),
				new SwitchWay(t1, t1).addSwitch(s2, !true).addSwitch(s1, !true),
				new SwitchWay(t1, t2).addSwitch(s2, false).addSwitch(s1, true));

		LocoNetMessageReciever.getInstance().registerListener(l -> {
			if (l == null)
				return;

			String params = "[";

			for (byte b : l.toLocoNetMessage().getValues())
				params += Byte.toString(b) + "|";

			params += "]";
			Ref.LOGGER.config(
					"Message Recieved: Type=" + l.toLocoNetMessage().getType().toString() + "; Parameter=" + params);

			try {
				for (ClientHandler ch : Server.getClientHandlers())
					ch.sendDatapacket(new Datapacket(DatapacketType.SERVER_SEND_MESSAGE, l));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (l instanceof SensorMessage) {
				SensorMessage message = (SensorMessage) l;

//				try {
//					new SwitchMessage((byte) 0, false).send();
//					new SwitchMessage((byte) 2, false).send();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

				Ref.LOGGER.info("Sensor: " + message.getAddress() + " " + message.getState());

//				for (Track t : tracks) {
//					if (t.getSensor().isAddress(message.getAddress(), message.getState())) {
//
//						if (t.getTrain() == null)
//							t.setTrain(this.trains.get(0));
//						t.getTrain().setNext();
//
//						// t.getSensor().onTrainDriveOut(trains.get(0));
//
//					}
//				}

			}

			if (l instanceof TrainSlotMessage) {
				TrainSlotMessage message = (TrainSlotMessage) l;

				Train train;
				this.trains.add(train = new Train(message.getAddress(), message.getSlot(), message.getSpeed()));

				train.setRoad(t1, t3, t1, t3, t2, t3, t0, t3);
				train.setTracks(t3, t0, t2);

				Platform.runLater(() -> {
					GUIServer.getInstance().trains.setItems(FXCollections.observableArrayList(this.trains));
				});
			}
		});

	}

}
