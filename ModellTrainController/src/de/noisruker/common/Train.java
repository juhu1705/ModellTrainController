package de.noisruker.common;

import java.io.Serializable;
import java.util.HashMap;

import de.noisruker.common.messages.SpeedMessage;
import de.noisruker.server.loconet.LocoNet;
import de.noisruker.util.Ref;

public class Train implements Serializable {

	/**
	 * Die universelle Serial-Version-UID
	 */
	private static final long serialVersionUID = Ref.UNIVERSAL_SERIAL_VERSION_UID;

	/**
	 * Die eindeutige Addresse unter der ein Zug eingespeichert ist.
	 */
	private byte address, slot;
	private byte speed, maxSpeed, normalSpeed, minSpeed, actualSpeed;
	boolean foreward, standartForeward, stopNext = false;

	public int lastPosition = -1, actualPosition = -1, destination = -1, startWay = -1, startPos = -1, waiting = 0;

	/**
	 * Generiert einen neuen {@link Train Zug} mit der entsprechenden addresse. Es
	 * kann nur einen Zug mit einer speuifischen Addresse geben.
	 *
	 * @param address Adresse des Zuges
	 */
	public Train(byte address, byte slot) {
		this(address, slot, (byte) 0);
	}

	public Train(byte address, byte slot, byte speed) {
		this(address, slot, speed, (byte) 124, (byte) 90, (byte) 35, true);
	}

	public Train(byte address, byte slot, byte speed, byte maxSpeed, byte normalSpeed, byte minSpeed,
			boolean standartForeward) {
		this.slot = slot;
		this.address = address;
		this.setMaxSpeed(maxSpeed);
		this.setNormalSpeed(normalSpeed);
		this.setMinSpeed(minSpeed);
		this.setStandartForeward(standartForeward);
		this.setSpeed(speed);
		this.setActualSpeed(speed);
	}

	public void setForeward(boolean foreward) {
		this.foreward = foreward;
	}

	private boolean setSpeed(byte speed) {
		if (speed > this.maxSpeed || speed < 0)
			return false;
		this.speed = speed;
		return true;
	}

	public void setNormalSpeed() {
		this.setActualSpeed(this.normalSpeed);
	}

	public void setMaxSpeed() {
		this.setActualSpeed(this.maxSpeed);
	}

	public void setBreakSpeed() {
		this.setActualSpeed(this.minSpeed);
	}

	public void stop() {
		this.setActualSpeed((byte) 0);
	}

	public boolean isStandartForeward() {
		return standartForeward;
	}

	public void setStandartForeward(boolean standartForeward) {
		this.standartForeward = standartForeward;
	}

	public byte getBreakSpeed() {
		return minSpeed;
	}

	public void setMinSpeed(byte brakeSpeed) {
		this.minSpeed = brakeSpeed;
	}

	public byte getNormalSpeed() {
		return normalSpeed;
	}

	public void setNormalSpeed(byte normalSpeed) {
		this.normalSpeed = normalSpeed;
	}

	public byte getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(byte maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public byte getActualSpeed() {
		return actualSpeed;
	}

	public void setActualSpeed(byte actualSpeed) {
		this.actualSpeed = actualSpeed;

		try {
			new SpeedMessage(this.getSlot(), this.actualSpeed).send();
		} catch (Exception e1) {
			Ref.LOGGER.severe("Failed to set speed, please try manually!");
		}
	}

	public byte getAddress() {
		return this.address;
	}

	public byte getSlot() {
		return this.slot;
	}

	public void trainEnter(int nodeAddress) {
		Railroad.Section position = LocoNet.getRailroad().getNodeByAddress(actualPosition);

		if(this.way != null && (this.way.containsKey(position) || (this.startWay != -1 && this.startPos != -1))) {
			if (this.actualPosition != nodeAddress && ((position.address == this.startPos &&
					position.nodeConnections.get(this.startWay).to == nodeAddress) ||
					(this.startWay == -1 && this.startPos == -1 &&
							position.nodeConnections.get(this.way.get(position)).to == nodeAddress))) {
				this.lastPosition = this.actualPosition;
				this.actualPosition = nodeAddress;

				if(this.startPos != -1 && this.startWay != -1) {
					LocoNet.getRailroad().getNodeByAddress(startPos).nodeConnections.get(startWay).activate();
				}

				this.startPos = -1;
				this.startWay = -1;

				Ref.LOGGER.info("Train " + this.getAddress() + " enter sensor " + nodeAddress);


				Railroad.Section node = LocoNet.getRailroad().getNodeByAddress(nodeAddress);

				if (node != null && way != null && way.containsKey(node))
					node.nodeConnections.get(way.get(node)).activate();

				if (nodeAddress == destination) {
					this.setBreakSpeed();

					this.stopNext = true;
				}

				this.reservateNext();
			}
		}
	}

	public void trainLeft(int nodeAddress) {
		if(this.stopNext && nodeAddress == this.lastPosition) {
			this.stop();
			this.stopNext = false;
			way = null;
			destination = -1;
			waiting = 10;
		}
		if(nodeAddress == this.lastPosition) {
			Ref.LOGGER.info("Train " + this.getAddress() + " left sensor " + nodeAddress);

			Railroad r = LocoNet.getRailroad();
			r.getNodeByAddress(this.lastPosition).reservated = null;
		}
	}

	public void update() {
		Railroad r = LocoNet.getRailroad();
		int trainsDriving = r.trainsWithDestination();

		Railroad.Section node = r.getNodeByAddress(actualPosition);

		if(way != null && actualSpeed == 0) {
			if(!way.containsKey(node)) {
				way = null;
				destination = -1;
				return;
			}

			Railroad.Section next = r.getNodeByAddress(node.nodeConnections.get(way.get(node)).to);

			if(next.reservated == null)
				next.reservated = this;

			if(next.reservated == this && way.containsKey(next)) {
				Railroad.Section toReserve = r.getNodeByAddress(next.nodeConnections.get(way.get(next)).to);

				if (toReserve.reservated == null)
					toReserve.reservated = this;

				if(toReserve.reservated == this) {
					this.setMaxSpeed();
					this.stopNext = false;
				}
			}
		} else if(waiting == 0 && way == null && destination == -1 && trainsDriving < Ref.autoDriveNumber &&
				Ref.rand.nextInt(trainsDriving == 0 ? 4 : trainsDriving == 1 ? 55 : 100) == 1) {
			Ref.LOGGER.info("Train " + this.address + " is ready to drive!");

			if(this.actualPosition == -1)
				this.actualPosition = 7;
			if(this.lastPosition == -1)
				this.lastPosition = 11;

			int newPosition;

			do {
				newPosition = LocoNet.getRailroad().nodes.get(Ref.rand.nextInt(LocoNet.getRailroad().nodes.size())).address;
			} while (newPosition == this.actualPosition || LocoNet.getRailroad().getNodeByAddress(newPosition).reservated != null);

			Ref.LOGGER.info("Calculate route from " + this.actualPosition + " to " + newPosition + " with start direction from " + this.lastPosition);

			LocoNet.getRailroad().calculateRailway(this.lastPosition, this.actualPosition, newPosition, this);

			if(way != null) {
				Ref.LOGGER.info("Start Auto Driving: ");
				Ref.LOGGER.info("Train " + this.getAddress() + " drive from " + this.actualPosition + " to " + (newPosition));

				this.setNormalSpeed();
			}
		}

		if(way == null)
			destination = -1;

		if(waiting > 0) {
			waiting--;
		} else if(waiting < 0)
			waiting = 0;

	}

	public HashMap<Railroad.Section, Integer> way = null;



	public void reservateNext() {
		if(way == null)
			return;

		Railroad r = LocoNet.getRailroad();

		Railroad.Section node = r.getNodeByAddress(actualPosition);

		if(node.reservated != this && node.reservated != null) {
			this.stop();
			node.reservated.stop();

			this.way = null;
			this.destination = -1;

			node.reservated.way = null;
			node.reservated.destination = -1;
			return;
		} else if(node.reservated == null)
			node.reservated = this;

		if(actualPosition != destination && way.containsKey(node)) {
			Railroad.Section next = r.getNodeByAddress(node.nodeConnections.get(way.get(node)).to);

			if(next.reservated != this && next.reservated != null) {
				this.stopNext = true;
				this.setBreakSpeed();

				this.way = null;
				this.destination = -1;
				return;
			} else if(next.reservated == null)
				next.reservated = this;

			if(next.address != destination) {
				Railroad.Section toReserve = r.getNodeByAddress(next.nodeConnections.get(way.get(next)).to);

				Train conflict;

				if((conflict = toReserve.reservate(this)) != this) {
					this.stopNext = true;
					this.setBreakSpeed();
					this.way = null;
					this.destination = -1;
				}
			}
		}
	}

	public void setPosition(int position) {
		this.actualPosition = position;
		LocoNet.getRailroad().getNodeByAddress(position).reservated = this;
	}

	public void setLastPosition(int lposition) {
		this.lastPosition = lposition;
	}
}
