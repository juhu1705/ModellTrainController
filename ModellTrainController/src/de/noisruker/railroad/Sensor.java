package de.noisruker.railroad;

public class Sensor {

	private final int address;
	private boolean state;

	public Sensor(int address, boolean state) {
		this.address = address;
		this.state = state;
	}

	public boolean isAddress(int address, boolean b) {
		return this.address == address && this.state == b;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Sensor))
			return false;
		Sensor s = (Sensor) obj;

		return s.address == this.address;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public int getAddress() {
		return this.address;
	}

	public boolean getState() {
		return this.state;
	}

}
