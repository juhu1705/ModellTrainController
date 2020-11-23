package de.noisruker.loconet;

import de.noisruker.loconet.LocoNetConnection.PortNotOpenException;
import de.noisruker.util.Util;
import jssc.SerialPortException;

public class LocoNetMessageSender {

	/**
	 * Die aktuelle Instanz dieser Klasse.
	 */
	private static LocoNetMessageSender instance;

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

	public void sendMessage(byte... message) throws SerialPortException, PortNotOpenException {
		this.connection.send(Util.addCheckSum(message));
	}

	public void useConnection(LocoNetConnection connection) {
		if (connection != null)
			this.connection = connection;
	}

	public void sendMessage(byte opCode, byte... values) throws SerialPortException, PortNotOpenException {
		this.sendMessage(Util.addOpCode(opCode, values));
	}
}
