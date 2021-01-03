package de.noisruker.loconet;

import de.noisruker.loconet.LocoNetConnection.PortNotOpenException;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import jssc.SerialPortException;

import java.util.ArrayList;
import java.util.logging.Level;

public class LocoNetMessageSender {

	/**
	 * Die aktuelle Instanz dieser Klasse.
	 */
	private static LocoNetMessageSender instance;

	private static ArrayList<byte[]> messages = new ArrayList<>();

	private static boolean isRunning = false;

	/**
	 * Gibt die aktuelle Instanz dieser Klasse zur�ck
	 * 
	 * @return {@link #instance Die aktive Instanz dieser Klasse}, oder eine neue,
	 *         wenn keine Instanz vorhanden ist.
	 */
	public static LocoNetMessageSender getInstance() {
		return LocoNetMessageSender.instance == null ? LocoNetMessageSender.instance = new LocoNetMessageSender()
				: LocoNetMessageSender.instance;
	}

	private LocoNetConnection connection;

	protected LocoNetMessageSender() {
	}

	public void sendMessage(byte... message) {
		messages.add(Util.addCheckSum(message));

		this.sendMessages();
		//this.connection.send(Util.addCheckSum(message));
	}

	public boolean areAllMessagesSend() {
		return messages.isEmpty();
	}

	public void sendMessages() {
		if(isRunning)
			return;
		isRunning = true;
		new Thread(() -> {
			while(true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ignored) { }
				while (!messages.isEmpty()) {
					int i = 0;
					while (!LocoNetMessageReceiver.messageChecked()) {
						if(i == 5)
							break;
						i++;
						try {
							Thread.sleep(100);
						} catch (InterruptedException ignored) { }
					}

					if (messages.size() < 1)
						break;
					byte[] message = messages.remove(0);
					LocoNetMessageReceiver.setCheckMessage(message);
					try {
						this.connection.send(Util.addCheckSum(message));
					} catch (PortNotOpenException | SerialPortException e) {
						Ref.LOGGER.log(Level.SEVERE, "LocoNet Connection failed", e);
					}
				}
			}
		}).start();
	}

	public void useConnection(LocoNetConnection connection) {
		if (connection != null)
			this.connection = connection;
	}

	public void sendMessage(byte opCode, byte... values) {
		this.sendMessage(Util.addOpCode(opCode, values));
	}
}
