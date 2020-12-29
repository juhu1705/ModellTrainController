package de.noisruker.loconet.messages;

import static de.noisruker.loconet.messages.MessageType.OPC_LOCO_SPD;

import java.io.IOException;
import java.io.Serializable;

public class DirectionMessage implements AbstractMessage, Serializable {

	private final boolean forward;
	private byte function;
	private final byte slot;

	public DirectionMessage(byte slot, boolean forward) {
		this.slot = slot;
		this.forward = forward;
	}

	public DirectionMessage(byte slot, byte function) {
		this.slot = slot;
		this.forward = (function == 0);
		this.function = function;
	}

	public byte getFunction() {
		return this.function;
	}

	@Override
	public LocoNetMessage toLocoNetMessage() {
		if (this.forward)
			return new LocoNetMessage(OPC_LOCO_SPD, slot, (byte) 0);
		else
			return new LocoNetMessage(OPC_LOCO_SPD, slot, (byte) 32);
	}

	@Override
	public void send() throws IOException {
		this.toLocoNetMessage().send();
	}

}
