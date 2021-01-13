package de.noisruker.railroad.elements;

import de.noisruker.gui.GuiMain;
import de.noisruker.gui.RailroadImages;
import de.noisruker.loconet.messages.AbstractMessage;
import de.noisruker.loconet.messages.SwitchMessage;
import de.noisruker.railroad.Position;
import de.noisruker.railroad.RailRotation;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Switch extends AbstractRailroadElement {

	private static ArrayList<Switch> allSwitches = new ArrayList<>();

	public static ArrayList<Switch> getAllSwitches() {
		return allSwitches;
	}

	private static void addSwitch(Switch s) {
		for(int i = 0; i < allSwitches.size(); i++) {
			if (allSwitches.get(i).address > s.address) {
				allSwitches.add(i, s);
				return;
			}
		}
		allSwitches.add(s);
	}

	private boolean state;
	private final byte address;

	private final SwitchType type;
	private final boolean normalPosition;

	public Switch(byte address, SwitchType type, RailRotation rotation, boolean normalPosition, Position position) {
		super("switch", position, rotation);
		this.address = address;
		this.type = type;
		this.normalPosition = normalPosition;
		Switch.addSwitch(this);
	}

	public SwitchMessage getMessage(boolean state) {
		return new SwitchMessage(address, state);
	}

	private void setState(boolean state) {
		this.state = state;
	}

	public void setAndUpdateState(boolean state) {
		this.getMessage(state).toLocoNetMessage().send();
	}

	@Override
	public void saveTo(BufferedWriter writer) throws IOException {
		super.saveTo(writer);
		Util.writeParameterToBuffer("address", Integer.toString(this.address));
		Util.writeParameterToBuffer("normal_state", Boolean.toString(normalPosition));
		Util.writeParameterToBuffer("switch_type", type.name());
		Util.closeWriting();
	}

	@Override
	public Image getImage() {
		switch (type) {
			case LEFT:
				switch (rotation) {
					case NORTH:
						if(!normalPosition) {
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
						if(!normalPosition) {
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
						if(!normalPosition) {
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
						if(!normalPosition) {
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
						if(!normalPosition) {
							if (state)
								return RailroadImages.SWITCH_NORTH_RIGHT_ON;
							else
								return RailroadImages.SWITCH_NORTH_STRAIGHT_RIGHT_OFF;
						} else {
							if (state)
								return RailroadImages.SWITCH_NORTH_STRAIGHT_RIGHT_ON;
							else
								return RailroadImages.SWITCH_NORTH_RIGHT_OFF;
						}
					case SOUTH:
						if(!normalPosition) {
							if (state)
								return RailroadImages.SWITCH_SOUTH_RIGHT_ON;
							else
								return RailroadImages.SWITCH_SOUTH_STRAIGHT_RIGHT_OFF;
						} else {
							if (state)
								return RailroadImages.SWITCH_SOUTH_STRAIGHT_RIGHT_ON;
							else
								return RailroadImages.SWITCH_SOUTH_RIGHT_OFF;
						}
					case EAST:
						if(!normalPosition) {
							if (state)
								return RailroadImages.SWITCH_EAST_RIGHT_ON;
							else
								return RailroadImages.SWITCH_EAST_STRAIGHT_RIGHT_OFF;
						} else {
							if (state)
								return RailroadImages.SWITCH_EAST_STRAIGHT_RIGHT_ON;
							else
								return RailroadImages.SWITCH_EAST_RIGHT_OFF;
						}
					case WEST:
						if(!normalPosition) {
							if (state)
								return RailroadImages.SWITCH_WEST_RIGHT_ON;
							else
								return RailroadImages.SWITCH_WEST_STRAIGHT_RIGHT_OFF;
						} else {
							if (state)
								return RailroadImages.SWITCH_WEST_STRAIGHT_RIGHT_ON;
							else
								return RailroadImages.SWITCH_WEST_RIGHT_OFF;
						}
				}
				break;
			case LEFT_RIGHT:
				switch (rotation) {
					case NORTH:
						if(!normalPosition) {
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
						if(!normalPosition) {
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
						if(!normalPosition) {
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
						if(!normalPosition) {
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
			if(switchMessage.getAddress() == this.address) {
				this.state = switchMessage.getState();
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
		if(!isSwitchPossible(from))
			switch (rotation) {
				case NORTH:
					return new Position(super.position.getX(), super.position.getY() - 1);
				case WEST:
					return new Position(super.position.getX() - 1, super.position.getY());
				case EAST:
					return new Position(super.position.getX() + 1, super.position.getY());
				case SOUTH:
					return new Position(super.position.getX(), super.position.getY() + 1);
			}
		if(isPositionValid(from))
			return getNextPositionSwitchSpecial(from, this.state);
		return getNextPositionSwitchSpecial(from, !this.state);
	}

	public boolean isPositionValid(Position from) {
		switch (rotation) {
			case NORTH:
				switch (type) {
					case LEFT:
						if((state && !normalPosition) || (!state && normalPosition))
							return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ||
									from.equals(new Position(super.position.getX() + 1, super.position.getY()));
						else
							return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ||
									from.equals(new Position(super.position.getX(), super.position.getY() + 1));
					case RIGHT:
						if((state && !normalPosition) || (!state && normalPosition))
							return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ||
									from.equals(new Position(super.position.getX() - 1, super.position.getY()));
						else
							return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ||
									from.equals(new Position(super.position.getX(), super.position.getY() + 1));
					case LEFT_RIGHT:
						if((state && !normalPosition) || (!state && normalPosition))
							return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ||
									from.equals(new Position(super.position.getX() - 1, super.position.getY()));
						else
							return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ||
									from.equals(new Position(super.position.getX() + 1, super.position.getY()));
				}
				break;
			case WEST:
				switch (type) {
					case LEFT:
						if((state && !normalPosition) || (!state && normalPosition))
							return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ||
									from.equals(new Position(super.position.getX() - 1, super.position.getY()));
						else
							return from.equals(new Position(super.position.getX() + 1, super.position.getY())) ||
									from.equals(new Position(super.position.getX() - 1, super.position.getY()));
					case RIGHT:
						if((state && !normalPosition) || (!state && normalPosition))
							return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ||
									from.equals(new Position(super.position.getX() - 1, super.position.getY()));
						else
							return from.equals(new Position(super.position.getX() + 1, super.position.getY())) ||
									from.equals(new Position(super.position.getX() - 1, super.position.getY()));
					case LEFT_RIGHT:
						if((state && !normalPosition) || (!state && normalPosition))
							return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ||
									from.equals(new Position(super.position.getX() - 1, super.position.getY()));
						else
							return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ||
									from.equals(new Position(super.position.getX() - 1, super.position.getY()));
				}
				break;
			case EAST:
				switch (type) {
					case LEFT:
						if((state && !normalPosition) || (!state && normalPosition))
							return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ||
									from.equals(new Position(super.position.getX() + 1, super.position.getY()));
						else
							return from.equals(new Position(super.position.getX() + 1, super.position.getY())) ||
									from.equals(new Position(super.position.getX() - 1, super.position.getY()));
					case RIGHT:
						if((state && !normalPosition) || (!state && normalPosition))
							return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ||
									from.equals(new Position(super.position.getX() + 1, super.position.getY()));
						else
							return from.equals(new Position(super.position.getX() + 1, super.position.getY())) ||
									from.equals(new Position(super.position.getX() - 1, super.position.getY()));
					case LEFT_RIGHT:
						if((state && !normalPosition) || (!state && normalPosition))
							return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ||
									from.equals(new Position(super.position.getX() + 1, super.position.getY()));
						else
							return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ||
									from.equals(new Position(super.position.getX() + 1, super.position.getY()));
				}
				break;
			case SOUTH:
				switch (type) {
					case LEFT:
						if((state && !normalPosition) || (!state && normalPosition))
							return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ||
									from.equals(new Position(super.position.getX() + 1, super.position.getY()));
						else
							return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ||
									from.equals(new Position(super.position.getX(), super.position.getY() + 1));
					case RIGHT:
						if((state && !normalPosition) || (!state && normalPosition))
							return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ||
									from.equals(new Position(super.position.getX() - 1, super.position.getY()));
						else
							return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ||
									from.equals(new Position(super.position.getX(), super.position.getY() + 1));
					case LEFT_RIGHT:
						if((state && !normalPosition) || (!state && normalPosition))
							return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ||
									from.equals(new Position(super.position.getX() - 1, super.position.getY()));
						else
							return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ||
									from.equals(new Position(super.position.getX() + 1, super.position.getY()));
				}
				break;
		}
		return false;
	}

	public Position getNextPositionSwitchSpecial(Position from, boolean state) {
		switch (type) {
			case LEFT:
				switch (rotation) {
					case NORTH:
						if(!normalPosition) {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX(), super.position.getY() + 1) :
										new Position(super.position.getX(), super.position.getY() - 1);
						} else {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX(), super.position.getY() + 1) :
										new Position(super.position.getX(), super.position.getY() - 1);
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
						}
					case SOUTH:
						if(!normalPosition) {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() + 1);
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX(), super.position.getY() - 1) :
										new Position(super.position.getX(), super.position.getY() + 1);
						} else {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX(), super.position.getY() - 1) :
										new Position(super.position.getX(), super.position.getY() + 1);
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() + 1);
						}
					case EAST:
						if(!normalPosition) {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() + 1);
							else
								return from.equals(new Position(super.position.getX() + 1, super.position.getY())) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX() + 1, super.position.getY());
						} else {
							if (state)
								return from.equals(new Position(super.position.getX() + 1, super.position.getY())) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX() + 1, super.position.getY());
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() + 1);
						}
					case WEST:
						if(!normalPosition) {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
							else
								return from.equals(new Position(super.position.getX() + 1, super.position.getY())) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX() + 1, super.position.getY());
						} else {
							if (state)
								return from.equals(new Position(super.position.getX() + 1, super.position.getY())) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX() + 1, super.position.getY());
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
						}
				}
				break;
			case RIGHT:
				switch (rotation) {
					case NORTH:
						if(!normalPosition) {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX(), super.position.getY() + 1) :
										new Position(super.position.getX(), super.position.getY() - 1);
						} else {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX(), super.position.getY() + 1) :
										new Position(super.position.getX(), super.position.getY() - 1);
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
						}
					case SOUTH:
						if(!normalPosition) {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() + 1);
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX(), super.position.getY() + 1) :
										new Position(super.position.getX(), super.position.getY() - 1);
						} else {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX(), super.position.getY() + 1) :
										new Position(super.position.getX(), super.position.getY() - 1);
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() + 1);
						}
					case EAST:
						if(!normalPosition) {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
							else
								return from.equals(new Position(super.position.getX() + 1, super.position.getY())) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX() + 1, super.position.getY());
						} else {
							if (state)
								return from.equals(new Position(super.position.getX() + 1, super.position.getY())) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX() + 1, super.position.getY());
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
						}
					case WEST:
						if(!normalPosition) {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() + 1);
							else
								return from.equals(new Position(super.position.getX() + 1, super.position.getY() + 1)) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX() + 1, super.position.getY());
						} else {
							if (state)
								return from.equals(new Position(super.position.getX() + 1, super.position.getY() + 1)) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX() + 1, super.position.getY());
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() + 1);
						}
				}
				break;
			case LEFT_RIGHT:
				switch (rotation) {
					case NORTH:
						if(!normalPosition) {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
						} else {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
						}
					case SOUTH:
						if(!normalPosition) {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() + 1);
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
						} else {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() + 1);
						}
					case EAST:
						if(!normalPosition) {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() + 1);
						} else {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() + 1);
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX() + 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
						}
					case WEST:
						if(!normalPosition) {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() + 1);
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() - 1)) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() - 1);
						} else {
							if (state)
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() + 1);
							else
								return from.equals(new Position(super.position.getX(), super.position.getY() + 1)) ?
										new Position(super.position.getX() - 1, super.position.getY()) :
										new Position(super.position.getX(), super.position.getY() + 1);
						}
				}
				break;
		}
		return from;
	}

	public boolean setSwitchTo(Position from, Position to) {
		Position pFrom = isSwitchPossible(from) ? from : to;
		Position pTo = isSwitchPossible(from) ? to : from;

		Ref.LOGGER.info("Udate switch " + this.address + "; From: " + from + "; to: " + to);
		Ref.LOGGER.info("If true go to " + getNextPositionSwitchSpecial(from, true));
		Ref.LOGGER.info("If false go to " + getNextPositionSwitchSpecial(from, false));

		Ref.LOGGER.info("If true go to " + getNextPositionSwitchSpecial(pFrom, true));
		Ref.LOGGER.info("If false go to " + getNextPositionSwitchSpecial(pFrom, false));

		if(getNextPositionSwitchSpecial(pFrom, true).equals(pTo)) {
			Ref.LOGGER.info("Set state to true");
			this.setAndUpdateState(true);
		} else if(getNextPositionSwitchSpecial(pFrom, false).equals(pTo)) {
			Ref.LOGGER.info("Set state to false");
			this.setAndUpdateState(false);
		} else
			return false;
		return isPositionValid(from);
	}

	public boolean isSwitchPossible(Position from) {
		switch (rotation) {
			case NORTH:
				return from.equals(new Position(super.position.getX(), super.position.getY() - 1));
			case WEST:
				return from.equals(new Position(super.position.getX() - 1, super.position.getY()));
			case EAST:
				return from.equals(new Position(super.position.getX() + 1, super.position.getY()));
			case SOUTH:
				return from.equals(new Position(super.position.getX(), super.position.getY() + 1));
		}
		return false;
	}

	public void changeDirection() {
		this.setAndUpdateState(!this.state);
	}

	public byte address() {
		return address;
	}

	public enum SwitchType {
		LEFT,
		RIGHT,
		LEFT_RIGHT
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Switch aSwitch = (Switch) o;
		return address == aSwitch.address;
	}

	@Override
	public int hashCode() {
		return Objects.hash(address);
	}
}
