package de.noisruker.loconet;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import de.noisruker.loconet.LocoNetConnection.PortNotOpenException;
import de.noisruker.loconet.messages.LocoNetMessage;
import de.noisruker.loconet.messages.MessageType;
import de.noisruker.util.Util;

public class LocoNetMessageReciever {

	/**
	 * Die aktuelle Instanz dieser Klasse.
	 */
	private static LocoNetMessageReciever instance;

	/**
	 * Gibt die aktuelle Instanz dieser Klasse zurück
	 * 
	 * @return {@link #instance Die aktive Instanz dieser Klasse}, oder eine neue,
	 *         wenn keine Instanz vorhanden ist.
	 */
	public static LocoNetMessageReciever getInstance() {
		return LocoNetMessageReciever.instance == null ? LocoNetMessageReciever.instance = new LocoNetMessageReciever()
				: LocoNetMessageReciever.instance;
	}

	private LocoNetConnection connection = null;
	private boolean shouldRunn = true;

	protected LocoNetMessageReciever() {
	}

	public void start() {
		this.shouldRunn = true;
		new Thread(() -> {
			while (shouldRunn) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
				}

				byte[] message = null;

				try {
					message = this.connection.removeNextMessage();
				} catch (PortNotOpenException e) {
					continue;
				}

				if (message == null)
					continue;

				MessageType type = Util.getMessageType(message[0]);

				byte[] values = new byte[message.length - 2];

				for (int i = 0; i < values.length; i++)
					values[i] = message[i + 1];

				for (LocoNetMessageListener listener : this.listeners)
					try {
						listener.progressMessage(new LocoNetMessage(type, values).toMessage());
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}

			}
		}).start();
	}

	public LocoNetMessageReciever useConnection(LocoNetConnection connection) {
		if (connection != null)
			this.connection = connection;
		return this;
	}

	public void removeConnection() {
		this.shouldRunn = false;
		this.connection = null;
	}

	private ArrayList<LocoNetMessageListener> listeners = new ArrayList<>();

	public void registerListener(LocoNetMessageListener listener) {
		this.listeners.add(listener);
	}

}
