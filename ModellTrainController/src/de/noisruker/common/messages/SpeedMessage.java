package de.noisruker.common.messages;

import static de.noisruker.server.loconet.messages.MessageType.OPC_LOCO_SPD;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;

import de.noisruker.client.Client;
import de.noisruker.net.Side;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketType;
import de.noisruker.server.loconet.LocoNetConnection.PortNotOpenException;
import de.noisruker.server.loconet.messages.LocoNetMessage;
import de.noisruker.util.Ref;
import jssc.SerialPortException;

public class SpeedMessage implements AbstractMessage, Serializable {

	private byte speed, slot;

	public SpeedMessage(byte slot, byte speed) {
		this.speed = speed;
		this.slot = slot;
	}

	public byte getSpeed() {
		return this.speed;
	}

	public byte getSlot() {
		return this.slot;
	}

	@Override
	public LocoNetMessage toLocoNetMessage() {
		return new LocoNetMessage(OPC_LOCO_SPD, slot, speed);
	}

	@Override
	public void send() throws IOException {
		if (Ref.side.equals(Side.SERVER))
			try {
				this.toLocoNetMessage().send();
			} catch (SerialPortException | PortNotOpenException e) {
				Ref.LOGGER.log(Level.SEVERE, "Server not opened", e);
			}
		else
			Client.getConnectionHandler().sendDatapacket(new Datapacket(DatapacketType.SEND_SPEED_MESSAGE, this));
	}

}
