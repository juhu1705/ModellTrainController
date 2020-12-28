package de.noisruker.loconet;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import de.noisruker.loconet.LocoNetConnection.PortNotOpenException;
import de.noisruker.loconet.messages.LocoNetMessage;
import de.noisruker.loconet.messages.MessageType;
import de.noisruker.util.Util;

public class LocoNetMessageReceiver {

	/**
	 * Die aktuelle Instanz dieser Klasse.
	 */
	private static LocoNetMessageReceiver instance;

	/**
	 * Gibt die aktuelle Instanz dieser Klasse zurück
	 * 
	 * @return {@link #instance Die aktive Instanz dieser Klasse}, oder eine neue,
	 *         wenn keine Instanz vorhanden ist.
	 */
	public static LocoNetMessageReceiver getInstance() {
		return LocoNetMessageReceiver.instance == null ? LocoNetMessageReceiver.instance = new LocoNetMessageReceiver()
				: LocoNetMessageReceiver.instance;
	}

	private LocoNetConnection connection = null;
	private boolean shouldRun = true;

	private static byte[] checkMessage = null;

	public static void setCheckMessage(byte... message) {
		checkMessage = message;
	}

	public static boolean messageChecked() {
		return checkMessage == null;
	}

	protected LocoNetMessageReceiver() {
	}

	public void start() {
		this.shouldRun = true;
		new Thread(() -> {
			while (shouldRun) {
				byte[] message = null;

				try {
					message = this.connection.removeNextMessage();
				} catch (PortNotOpenException e) {
					continue;
				}

				if (message == null)
					continue;

				if(Arrays.equals(message, checkMessage))
					checkMessage = null;

				MessageType type = Util.getMessageType(message[0]);

				byte[] values = new byte[message.length - 2];

				if (values.length >= 0) System.arraycopy(message, 1, values, 0, values.length);

				for (LocoNetMessageListener listener : this.listeners)
					try {
						listener.progressMessage(new LocoNetMessage(type, values).toMessage());
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}

			}
		}).start();
	}

	public LocoNetMessageReceiver useConnection(LocoNetConnection connection) {
		if (connection != null)
			this.connection = connection;
		return this;
	}

	public void removeConnection() {
		this.shouldRun = false;
		this.connection = null;
	}

	private final ArrayList<LocoNetMessageListener> listeners = new ArrayList<>();

	public void registerListener(LocoNetMessageListener listener) {
		this.listeners.add(listener);
	}

}
