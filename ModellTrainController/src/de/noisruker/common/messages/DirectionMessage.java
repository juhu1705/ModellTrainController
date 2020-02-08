package de.noisruker.common.messages;

import static de.noisruker.server.loconet.messages.MessageType.OPC_LOCO_SPD;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;

import de.noisruker.net.Side;
import de.noisruker.server.loconet.LocoNetConnection.PortNotOpenException;
import de.noisruker.server.loconet.messages.LocoNetMessage;
import de.noisruker.util.Ref;
import jssc.SerialPortException;

public class DirectionMessage implements AbstractMessage, Serializable {

	private boolean foreward;
	private byte slot;

	public DirectionMessage(byte slot, boolean foreward) {
		this.slot = slot;
		this.foreward = foreward;
	}

	public DirectionMessage(byte slot, byte funktion) {
		this.slot = slot;
		this.foreward = funktion == 0 ? false : true;
	}

	@Override
	public LocoNetMessage toLocoNetMessage() {
		if (this.foreward)
			return new LocoNetMessage(OPC_LOCO_SPD, slot, (byte) 4);
		else
			return new LocoNetMessage(OPC_LOCO_SPD, slot, (byte) 0);
	}

	@Override
	public void send() throws IOException {
		if (Ref.side.equals(Side.SERVER))
			try {
				this.toLocoNetMessage().send();
			} catch (SerialPortException | PortNotOpenException e) {
				Ref.LOGGER.log(Level.SEVERE, "Server not opened", e);
			}
	}

}
