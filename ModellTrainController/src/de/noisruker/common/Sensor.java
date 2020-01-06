package de.noisruker.common;

public class Sensor {

	private byte address;
	private int address1;
	private boolean state1;
	private byte state;

	private Track parent;

	public Sensor(byte address, byte state) {

		this.address = address;
		this.state = state;
	}

	public boolean isAddress(byte address, byte b) {
		return this.address == address && this.state == b;
	}

	public void onTrainDriveIn(Train t) {
		this.parent.onTrainDriveIn(t, this);
	}

	public void onTrainDriveOut(Train t) {
		this.parent.onTrainDriveOut(t, this);
	}

}
