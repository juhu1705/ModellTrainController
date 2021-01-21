package de.noisruker.loconet.messages;

import java.io.IOException;
import java.io.Serializable;

public class SensorMessage implements Serializable, AbstractMessage {

	private final byte state;
    private final byte buffer1;
    private final byte buffer2;
    private final byte sensor;
    private final byte sensorSection;
	private final int address;

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
	public void send() {

	}

	@Override
	public String toString() {
		return "[SensorMessage from Sensor " + address + " changed to state " + state + "]";
	}

}
