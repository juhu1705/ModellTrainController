package de.noisruker.loconet.messages;

import java.io.IOException;
import java.io.Serializable;

public class SensorMessage implements Serializable, AbstractMessage {

	private byte state, buffer1, buffer2, sensor, sensorSection;
	private int address;

	/**
	 * @param address
	 * @param state
	 */
	public SensorMessage(byte address, byte state) {
		this.buffer1 = address;
		this.buffer2 = state;

		byte state5 = state;

		state <<= 2;
		state >>>= 7;

		state *= -1;

		this.sensor = address;
		this.sensorSection = state;

		state5 >>>= 4;
		state5 <<= 7;
		state5 >>>= 7;

		this.state = state5;
		this.address = (this.sensor * 2) + this.sensorSection + 1;

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

	}

	@Override
	public String toString() {
		return "[SensorMessage from Sensor " + address + " changed to state " + state + "]";
	}

}
