package de.noisruker.loconet.messages;

import static de.noisruker.loconet.messages.MessageType.OPC_LOCO_SPD;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;

import de.noisruker.loconet.LocoNetConnection.PortNotOpenException;
import de.noisruker.util.Ref;
import jssc.SerialPortException;

public class SpeedMessage implements AbstractMessage, Serializable {

	private final byte speed;
    private final byte slot;

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
		try {
			this.toLocoNetMessage().send();
		} catch (SerialPortException | PortNotOpenException e) {
			Ref.LOGGER.log(Level.SEVERE, "Server not opened", e);
		}
	}

	@Override
	public String toString() {
		return "[SpeedMessage for Train " + slot + " to speed " + speed + "]";
	}

}
