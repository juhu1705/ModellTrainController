package de.noisruker.railroad.elements;

import de.noisruker.gui.GuiMain;
import de.noisruker.gui.RailroadImages;
import de.noisruker.loconet.messages.AbstractMessage;
import de.noisruker.loconet.messages.SensorMessage;
import de.noisruker.railroad.Position;
import de.noisruker.railroad.RailRotation;
import de.noisruker.railroad.Train;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Sensor extends AbstractRailroadElement {

	private static ArrayList<Sensor> allSensors = new ArrayList<>();

	public static ArrayList<Sensor> getAllSensors() {
		return allSensors;
	}

	private static void addSensor(Sensor s) {
		for(int i = 0; i < allSensors.size(); i++) {
			if (allSensors.get(i).address > s.address) {
				allSensors.add(i, s);
				return;
			}
		}
		allSensors.add(s);
	}

	private final int address;

	private boolean state;

	private Train train, securitySavedTrain;
	private int cooldown = -1;

	public Sensor(int address, boolean state, Position position, RailRotation rotation) {
		super("sensor", position, rotation);
		this.address = address;
		this.state = state;
		Sensor.addSensor(this);
	}

	@Override
	public void saveTo(BufferedWriter writer) throws IOException {
		super.saveTo(writer);
		Util.writeParameterToBuffer("address", Integer.toString(address));
		Util.closeWriting();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Sensor sensor = (Sensor) o;
		return address == sensor.address;
	}

	public boolean addTrain(Train t) {
		if(this.train == null) {
			this.train = t;
			return true;
		}
		return false;
	}

	public void onTrainLeft(byte address) {
		if(address == this.getAddress()) {
			this.securitySavedTrain = this.trainLeft();
		}
	}

	public void onTrainEnter(byte address) {
		if(address == this.address) {
			if (securitySavedTrain != null && this.train == null)
				this.addTrain(securitySavedTrain);
			this.securitySavedTrain = null;
		}
	}

	public void update() {
		if(this.securitySavedTrain != null) {
			if(cooldown == -1)
				cooldown = 4;
			else {
				cooldown--;
				if(cooldown == 0)
					this.securitySavedTrain = null;
			}
		} else if(cooldown >= 0)
			cooldown = -1;
	}

	public Train trainLeft() {
		Train t = this.train;
		this.train = null;
		return t;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), address);
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
		return rotation.equals(RailRotation.NORTH) || rotation.equals(RailRotation.SOUTH) ?
				state ? RailroadImages.STRAIGHT_SENSOR_VERTICAL_OFF : RailroadImages.STRAIGHT_SENSOR_VERTICAL :
				state ? RailroadImages.STRAIGHT_SENSOR_HORIZONTAL_OFF : RailroadImages.STRAIGHT_SENSOR_HORIZONTAL;
	}

	@Override
	public void onLocoNetMessage(AbstractMessage message) {
		if(message instanceof SensorMessage) {
			SensorMessage m = (SensorMessage) message;
			if(m.getAddress() == this.getAddress()) {
				this.setState(m.getState());
				if(GuiMain.getInstance() != null)
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
			case NORTH, SOUTH:
				if(from.equals(new Position(position.getX(), position.getY() + 1)))
					return new Position(position.getX(), position.getY() - 1);
				else if(from.equals(new Position(position.getX(), position.getY() - 1)))
					return new Position(position.getX(), position.getY() + 1);
				break;
			case WEST, EAST:
				if(from.equals(new Position(position.getX() - 1, position.getY())))
					return new Position(position.getX() + 1, position.getY());
				else if(from.equals(new Position(position.getX() + 1, position.getY())))
					return new Position(position.getX() - 1, position.getY());
				break;
		}
		return null;
	}

	@Override
	public String toString() {
		return "Sensor: " + address;
	}

	public Train getTrain() {
		return this.train;
	}

	public boolean isFree(Train train) {
		if(this.train == train) return true;
		if(this.train != null) return false;
		return this.getState();
	}
}
