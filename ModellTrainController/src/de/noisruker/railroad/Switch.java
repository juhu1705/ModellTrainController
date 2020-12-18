package de.noisruker.railroad;

import de.noisruker.gui.RailroadImages;
import de.noisruker.loconet.messages.AbstractMessage;
import de.noisruker.loconet.messages.SwitchMessage;
import de.noisruker.loconet.LocoNetConnection.PortNotOpenException;
import javafx.scene.image.Image;
import jssc.SerialPortException;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.IOException;

public class Switch extends AbstractRailroadElement {

	private boolean state;
	private final byte address;

	private final SwitchType type;
	private final RailRotation rotation;
	private final boolean normalPosition;

	public Switch(byte address, SwitchType type, RailRotation rotation, boolean normalPosition) {
		super("switch");
		this.address = address;
		this.type = type;
		this.rotation = rotation;
		this.normalPosition = normalPosition;

		//this.setAndUpdateState(true);
	}

	public SwitchMessage getMessage() {
		return new SwitchMessage(address, state);
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public void setAndUpdateState(boolean state) {
		this.setState(state);
		try {
			this.getMessage().toLocoNetMessage().send();
		} catch (SerialPortException | PortNotOpenException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveTo(BufferedWriter writer) throws IOException {

	}

	@Override
	public Image getImage() {
		switch (type) {
			case LEFT:
				switch (rotation) {
					case NORTH:
						if(normalPosition) {
							if (state)
								return RailroadImages.SWITCH_NORTH_LEFT_ON;
							else
								return RailroadImages.SWITCH_NORTH_STRAIGHT_LEFT_OFF;
						} else {
							if (state)
								return RailroadImages.SWITCH_NORTH_STRAIGHT_LEFT_ON;
							else
								return RailroadImages.SWITCH_NORTH_LEFT_OFF;
						}
					case SOUTH:
						if(normalPosition) {
							if (state)
								return RailroadImages.SWITCH_SOUTH_LEFT_ON;
							else
								return RailroadImages.SWITCH_SOUTH_STRAIGHT_LEFT_OFF;
						} else {
							if (state)
								return RailroadImages.SWITCH_SOUTH_STRAIGHT_LEFT_ON;
							else
								return RailroadImages.SWITCH_SOUTH_LEFT_OFF;
						}
					case EAST:
						if(normalPosition) {
							if (state)
								return RailroadImages.SWITCH_EAST_LEFT_ON;
							else
								return RailroadImages.SWITCH_EAST_STRAIGHT_LEFT_OFF;
						} else {
							if (state)
								return RailroadImages.SWITCH_EAST_STRAIGHT_LEFT_ON;
							else
								return RailroadImages.SWITCH_EAST_LEFT_OFF;
						}
					case WEST:
						if(normalPosition) {
							if (state)
								return RailroadImages.SWITCH_WEST_LEFT_ON;
							else
								return RailroadImages.SWITCH_WEST_STRAIGHT_LEFT_OFF;
						} else {
							if (state)
								return RailroadImages.SWITCH_WEST_STRAIGHT_LEFT_ON;
							else
								return RailroadImages.SWITCH_WEST_LEFT_OFF;
						}
				}
				break;
			case RIGHT:
				switch (rotation) {
					case NORTH:
						if(normalPosition) {
							if (state)
								return RailroadImages.SWITCH_NORTH_RIGHT_ON;
							else
								return RailroadImages.SWITCH_NORTH_LEFT_OFF;
						} else {
							if (state)
								return RailroadImages.SWITCH_NORTH_LEFT_ON;
							else
								return RailroadImages.SWITCH_NORTH_RIGHT_OFF;
						}
					case SOUTH:
						if(normalPosition) {
							if (state)
								return RailroadImages.SWITCH_SOUTH_RIGHT_ON;
							else
								return RailroadImages.SWITCH_SOUTH_LEFT_OFF;
						} else {
							if (state)
								return RailroadImages.SWITCH_SOUTH_LEFT_ON;
							else
								return RailroadImages.SWITCH_SOUTH_RIGHT_OFF;
						}
					case EAST:
						if(normalPosition) {
							if (state)
								return RailroadImages.SWITCH_EAST_RIGHT_ON;
							else
								return RailroadImages.SWITCH_EAST_LEFT_OFF;
						} else {
							if (state)
								return RailroadImages.SWITCH_EAST_LEFT_ON;
							else
								return RailroadImages.SWITCH_EAST_RIGHT_OFF;
						}
					case WEST:
						if(normalPosition) {
							if (state)
								return RailroadImages.SWITCH_WEST_RIGHT_ON;
							else
								return RailroadImages.SWITCH_WEST_LEFT_OFF;
						} else {
							if (state)
								return RailroadImages.SWITCH_WEST_LEFT_ON;
							else
								return RailroadImages.SWITCH_WEST_RIGHT_OFF;
						}
				}
				break;
			case LEFT_RIGHT:
				switch (rotation) {
					case NORTH:
						if(normalPosition) {
							if (state)
								return RailroadImages.SWITCH_NORTH_RIGHT_ON;
							else
								return RailroadImages.SWITCH_NORTH_LEFT_OFF;
						} else {
							if (state)
								return RailroadImages.SWITCH_NORTH_LEFT_ON;
							else
								return RailroadImages.SWITCH_NORTH_RIGHT_OFF;
						}
					case SOUTH:
						if(normalPosition) {
							if (state)
								return RailroadImages.SWITCH_SOUTH_RIGHT_ON;
							else
								return RailroadImages.SWITCH_SOUTH_LEFT_OFF;
						} else {
							if (state)
								return RailroadImages.SWITCH_SOUTH_LEFT_ON;
							else
								return RailroadImages.SWITCH_SOUTH_RIGHT_OFF;
						}
					case EAST:
						if(normalPosition) {
							if (state)
								return RailroadImages.SWITCH_EAST_RIGHT_ON;
							else
								return RailroadImages.SWITCH_EAST_LEFT_OFF;
						} else {
							if (state)
								return RailroadImages.SWITCH_EAST_LEFT_ON;
							else
								return RailroadImages.SWITCH_EAST_RIGHT_OFF;
						}
					case WEST:
						if(normalPosition) {
							if (state)
								return RailroadImages.SWITCH_WEST_RIGHT_ON;
							else
								return RailroadImages.SWITCH_WEST_LEFT_OFF;
						} else {
							if (state)
								return RailroadImages.SWITCH_WEST_LEFT_ON;
							else
								return RailroadImages.SWITCH_WEST_RIGHT_OFF;
						}
				}
				break;
		}
		return RailroadImages.EMPTY;
	}

	@Override
	public void onLocoNetMessage(AbstractMessage message) {
		if(message instanceof SwitchMessage) {
			SwitchMessage switchMessage = (SwitchMessage) message;
			if(switchMessage.getAddress() == this.address)
				this.state = switchMessage.getState();
		}
	}

	public enum SwitchType {
		LEFT,
		RIGHT,
		LEFT_RIGHT
	}

}
