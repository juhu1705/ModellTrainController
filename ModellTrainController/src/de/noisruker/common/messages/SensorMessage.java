package de.noisruker.common.messages;

import java.io.IOException;
import java.io.Serializable;

import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketType;
import de.noisruker.server.ClientHandler;
import de.noisruker.server.Server;
import de.noisruker.server.loconet.messages.LocoNetMessage;
import de.noisruker.server.loconet.messages.MessageType;
import de.noisruker.util.Ref;

public class SensorMessage implements Serializable, AbstractMessage {

	private byte state, buffer1, buffer2;
	private int address;

	/**
	 * @param address
	 * @param state
	 */
	public SensorMessage(byte address, byte state) {
		this.buffer1 = address;
		this.buffer2 = state;

		byte state5 = state;

		state >>= 5;

		int i = address;

		state5 >>>= 4;
		state5 <<= 7;
		state5 >>>= 7;

		i <<= 2;

		i |= state;

		this.state = state5;
		this.address = i;

	}

	public int getAddress() {
		return this.address;
	}

	public boolean getState() {
		return this.state == -1;
	}

	@Override
	public LocoNetMessage toLocoNetMessage() {
		return new LocoNetMessage(MessageType.OPC_INPUT_REP, buffer1, buffer2);
	}

	@Override
	public void send() throws IOException {
		for (ClientHandler c : Server.getClientHandlers())
			c.sendDatapacket(new Datapacket(DatapacketType.SERVER_SEND_MESSAGE, this));
	}

	@Override
	public String toString() {
		return "[SensorMessage from Sensor " + address + " changed to state " + state + "]";
	}

}
