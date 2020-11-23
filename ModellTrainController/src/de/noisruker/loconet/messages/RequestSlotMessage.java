package de.noisruker.loconet.messages;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;

import de.noisruker.loconet.LocoNetConnection.PortNotOpenException;
import de.noisruker.util.Ref;
import jssc.SerialPortException;

public class RequestSlotMessage implements Serializable, AbstractMessage {

	byte address;

	public RequestSlotMessage(byte address) {
		this.address = address;
	}

	@Override
	public LocoNetMessage toLocoNetMessage() {
		return new LocoNetMessage(MessageType.OPC_LOCO_ADR, (byte) 0, address);
	}

	@Override
	public void send() throws IOException {

		try {
			this.toLocoNetMessage().send();
		} catch (SerialPortException | PortNotOpenException e) {
			Ref.LOGGER.log(Level.SEVERE, "Server not opened", e);
		}
	}

}
