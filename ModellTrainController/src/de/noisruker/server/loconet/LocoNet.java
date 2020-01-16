package de.noisruker.server.loconet;

import java.io.IOException;
import java.util.ArrayList;

import de.noisruker.common.Track;
import de.noisruker.common.Train;
import de.noisruker.common.messages.TrainSlotMessage;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketType;
import de.noisruker.server.ClientHandler;
import de.noisruker.server.Server;
import de.noisruker.server.loconet.messages.LocoNetMessage;
import de.noisruker.util.Ref;
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

	public void stop() throws SerialPortException {
		LocoNetMessageReciever.getInstance().removeConnection();
		this.connection.close();
	}

	public void start(String port) throws SerialPortException {
		this.connection = new LocoNetConnection(port);
		this.connection.open();

		LocoNetMessageSender.getInstance().useConnection(this.connection);
		LocoNetMessageReciever.getInstance().useConnection(this.connection).start();

		LocoNetMessage.registerStandart();

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
				e.printStackTrace();
			}

			if (l instanceof TrainSlotMessage) {
				Ref.LOGGER.info("Write requested Train to slot: " + ((TrainSlotMessage) l).getSlot());
			}
		});

	}

}
