package de.noisruker.railroad;

import de.noisruker.gui.RailroadImages;
import de.noisruker.loconet.messages.AbstractMessage;
import javafx.scene.image.Image;

import java.io.BufferedWriter;
import java.io.IOException;

public class Sensor extends AbstractRailroadElement {

	private final int address;
	private int posX, posY;

	private boolean state;

	public Sensor(int address, boolean state) {
		super("sensor");
		this.address = address;
		this.state = state;
	}

	@Override
	public void saveTo(BufferedWriter writer) throws IOException {
		writer.append("");
		writer.append("");
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

	@Override
	public Image getImage() {
		return RailroadImages.STRAIGHT_SENSOR_HORIZONTAL;
	}

	@Override
	public void onLocoNetMessage(AbstractMessage message) {

	}
}
