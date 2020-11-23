package de.noisruker.loconet.messages;

import static de.noisruker.loconet.messages.MessageType.OPC_LOCO_SPD;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;

import de.noisruker.loconet.LocoNetConnection.PortNotOpenException;
import de.noisruker.util.Ref;
import jssc.SerialPortException;

public class DirectionMessage implements AbstractMessage, Serializable {

	private boolean foreward;
	private byte funktion;
	private byte slot;

	public DirectionMessage(byte slot, boolean foreward) {
		this.slot = slot;
		this.foreward = foreward;
	}

	public DirectionMessage(byte slot, byte funktion) {
		this.slot = slot;
		this.foreward = (funktion == 0 ? true : false);
		this.funktion = funktion;
	}

	public byte getFunktion() {
		return this.funktion;
	}

	@Override
	public LocoNetMessage toLocoNetMessage() {
		if (this.foreward)
			return new LocoNetMessage(OPC_LOCO_SPD, slot, (byte) 0);
		else
			return new LocoNetMessage(OPC_LOCO_SPD, slot, (byte) 32);
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
