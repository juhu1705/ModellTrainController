package de.noisruker.railroad.elements;

import de.noisruker.gui.GuiMain;
import de.noisruker.gui.RailroadImages;
import de.noisruker.loconet.messages.AbstractMessage;
import de.noisruker.loconet.messages.SensorMessage;
import de.noisruker.railroad.AbstractRailroadElement;
import de.noisruker.railroad.Position;
import de.noisruker.railroad.RailRotation;
import de.noisruker.util.Util;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Sensor extends AbstractRailroadElement {

	private static ArrayList<Sensor> allSensors = new ArrayList<>();

	public static ArrayList<Sensor> getAllSensors() {
		return allSensors;
	}

	private final int address;

	private boolean state;

	public Sensor(int address, boolean state, Position position, RailRotation rotation) {
		super("sensor", position, rotation);
		this.address = address;
		this.state = state;
		Sensor.allSensors.add(this);
		Platform.runLater(() -> GuiMain.getInstance().updateSensors());
	}

	@Override
	public void saveTo(BufferedWriter writer) throws IOException {
		super.saveTo(writer);
		Util.writeParameterToBuffer("address", Integer.toString(address));
		Util.closeWriting();
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
		return rotation.equals(RailRotation.NORTH) || rotation.equals(RailRotation.SOUTH) ? RailroadImages.STRAIGHT_SENSOR_VERTICAL : RailroadImages.STRAIGHT_SENSOR_HORIZONTAL;
	}

	@Override
	public void onLocoNetMessage(AbstractMessage message) {
		if(message instanceof SensorMessage) {
			SensorMessage m = (SensorMessage) message;
			if(m.getAddress() == this.getAddress()) {
				this.setState(m.getState());
				Platform.runLater(() -> {
					GuiMain gui = GuiMain.getInstance();
					HBox box = gui.railroadLines.get(this.position.getY());
					gui.railroadCells.get(box).get(this.position.getX()).setImage(this.getImage());
				});
			}
		}
	}

	@Override
	public Position getToPos(Position from) {
		switch (rotation) {
			case NORTH:
			case SOUTH:
				if(from.equals(new Position(position.getX(), position.getY() + 1)))
					return new Position(position.getX(), position.getY() - 1);
				else if(from.equals(new Position(position.getX(), position.getY() - 1)))
					return new Position(position.getX(), position.getY() + 1);
				break;
			case WEST:
			case EAST:
				if(from.equals(new Position(position.getX() - 1, position.getY())))
					return new Position(position.getX() + 1, position.getY());
				else if(from.equals(new Position(position.getX() + 1, position.getY())))
					return new Position(position.getX() - 1, position.getY());
				break;
		}
		return null;
	}
}
