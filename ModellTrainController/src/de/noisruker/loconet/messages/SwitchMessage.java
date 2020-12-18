package de.noisruker.loconet.messages;

import static de.noisruker.loconet.messages.MessageType.OPC_SW_REQ;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;

import de.noisruker.loconet.LocoNetConnection.PortNotOpenException;
import de.noisruker.util.Ref;
import jssc.SerialPortException;

public class SwitchMessage implements AbstractMessage, Serializable {

	private final byte address;
	private final boolean on;

	public SwitchMessage(byte address, boolean state) {
		this.address = address;

		this.on = state;
	}

	public byte getAddress() {
		return address;
	}

	public boolean getState() {
		return on;
	}

	@Override
	public void send() throws IOException {
		try {
			this.toLocoNetMessage().send();
		} catch (SerialPortException | PortNotOpenException e) {
			Ref.LOGGER.log(Level.SEVERE, "Server not opened", e);
		}
	}

	@Override
	public LocoNetMessage toLocoNetMessage() {
		if (on)
			return new LocoNetMessage(OPC_SW_REQ, address, (byte) 16);
		else
			return new LocoNetMessage(OPC_SW_REQ, address, (byte) 48);
	}

	@Override
	public String toString() {
		return "[SwitchMessage: Switch switch " + address + " to " + on + "]";
	}

}
