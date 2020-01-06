package de.noisruker.common.messages;

import java.io.IOException;
import java.io.Serializable;

import de.noisruker.server.loconet.messages.LocoNetMessage;
import de.noisruker.server.loconet.messages.MessageType;

public class TrainSlotMessage implements AbstractMessage, Serializable {

	private byte slot, slotStatus, address, speed, direction, track, slotstate, longAddress, sound, id1, id2;

	public TrainSlotMessage(byte... informations) {
		if (informations.length < 3)
			return;

		this.slot = informations[0];
		this.slotstate = informations[1];
		this.address = informations[2];

		if (informations.length < 11)
			return;

		this.speed = informations[3];
		this.direction = informations[4];
		this.track = informations[5];
		this.slotstate = informations[6];
		this.longAddress = informations[7];
		this.sound = informations[8];
		this.id1 = informations[9];
		this.id2 = informations[10];

	}

	public byte getSlot() {
		return this.slot;
	}

	public byte getAddress() {
		return this.address;
	}

	public byte getSpeed() {
		return this.speed;
	}

	public byte getTrack() {
		return this.track;
	}

	@Override
	public LocoNetMessage toLocoNetMessage() {
		return new LocoNetMessage(MessageType.OPC_SL_RD_DATA, (byte) 0x0E, this.slot, this.slotStatus, this.address,
				this.speed, this.direction, this.track, this.slotstate, this.longAddress, this.sound, this.id1,
				this.id2);
	}

	@Override
	public void send() throws IOException {

	}

}
