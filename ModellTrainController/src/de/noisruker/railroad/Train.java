package de.noisruker.railroad;

import java.io.Serializable;
import java.util.HashMap;

import de.noisruker.loconet.LocoNetConnection;
import de.noisruker.loconet.messages.LocoNetMessage;
import de.noisruker.loconet.messages.MessageType;
import de.noisruker.loconet.messages.SpeedMessage;
import de.noisruker.loconet.LocoNet;
import de.noisruker.loconet.messages.TrainSlotMessage;
import de.noisruker.util.Ref;
import jssc.SerialPortException;

public class Train implements Serializable, Comparable<Train> {

	/**
	 * This methods requests the slot of a Train from the LocoNet. Before you use this method make sure you
	 * {@link LocoNet#start(String) connected your LocoNet}. After this method is called the LocoNet answer with a
	 * {@link TrainSlotMessage train slot message}. This method will be automatically processed by the LocoNet and the
	 * new train will be added to the {@link LocoNet#getTrains() list of the trains}.
	 *
	 * @param address The address of the train
	 * @throws SerialPortException If there appears an error while sending the data to the LocoNet
	 * @throws LocoNetConnection.PortNotOpenException If the port is not open
	 */
	public static void addTrain(byte address) throws SerialPortException, LocoNetConnection.PortNotOpenException {
		if(!LocoNet.getInstance().getTrains().contains(Byte.valueOf(address)))
			new LocoNetMessage(MessageType.OPC_LOCO_ADR, (byte) 0, address).send();
	}

	/**
	 * The universal Serial-Version-UID
	 */
	private static final long serialVersionUID = Ref.UNIVERSAL_SERIAL_VERSION_UID;

	/**
	 * The unique address of a train.
	 */
	private final byte address;

	/**
	 * The slot that is given to the train from the LocoNet
	 */
	private final byte slot;

	/**
	 * The given name of the train
	 */
	private String name;

	/**
	 * Speed parameters
	 */
	private byte speed, maxSpeed, normalSpeed, minSpeed, actualSpeed;

	/**
	 * Direction parameters
	 */
	protected boolean forward, standardForward;

	/**
	 * If the train should stop after a complete drive in in the Railroad section
	 */
	protected boolean stopNext = false;

	/**
	 * Parameters for the auto drive progress
	 */
	protected int lastPosition = -1, actualPosition = -1, destination = -1, startWay = -1, startPos = -1, waiting = 0, useConnection = -1;

	/**
	 * The way to the next stop of this train
	 */
	public HashMap<Railroad.Section, Integer> way = null;

	/**
	 * Adds a train with the given slot and address. This Method is only called by {@link LocoNet the LocoNet instance}
	 * after receiving a {@link TrainSlotMessage Train slot message} from the LocoNet. If you want to add a train please
	 * use {@link Train#addTrain(byte) Train.addTrain(byte address)}. Then wait until the train is listed in
	 * {@link LocoNet#getTrains() LocoNet's trains list}.
	 *
	 * @param address The address of the train
	 * @param slot The given slot from the LocoNet to address this train.
	 *
	 * @implNote A Train was initialized with the followed parameters:
	 * <ul>
	 * <li>{@link Train#speed The trains speed is 0}</li>
	 * <li>{@link Train#maxSpeed The trains maximal speed is 124}</li>
	 * <li>{@link Train#normalSpeed The trains normal speed is 90}</li>
	 * <li>{@link Train#minSpeed The trains minimal speed is 35}</li>
	 * <li>{@link Train#standardForward The trains direction is set to true.} Means the train drives forward.
	 * Some trains drives normally "backwards" so you can set this to false to tell the program that the forward direction of your train is reversed.</li>
	 * </ul>
	 */
	public Train(byte address, byte slot) {
		this(address, slot, (byte) 0);
	}

	private Train(byte address, byte slot, byte speed) {
		this(address, slot, speed, (byte) 124, (byte) 90, (byte) 35, true);
	}

	private Train(byte address, byte slot, byte speed, byte maxSpeed, byte normalSpeed, byte minSpeed,
			boolean standardForward) {
		if(LocoNet.getInstance().getTrains().contains(address))
			throw new UnsupportedOperationException("You cannot add an existing Train.");

		this.slot = slot;
		this.address = address;
		this.setName("Train " + this.getAddress());
		this.setMaxSpeed(maxSpeed);
		this.setNormalSpeed(normalSpeed);
		this.setMinSpeed(minSpeed);
		this.setStandardForward(standardForward);
		this.setSpeed(speed);
		this.setActualSpeed(speed);
	}

	/* Setter Methods */

	/**
	 * Sets the standard parameters of a train
	 *
	 * @param name The given name for this train
	 * @param maxSpeed The fastest speed this train could drive with
	 * @param normalSpeed The normal speed the train would drive with
	 * @param minSpeed The slowest speed of the train. Used to stop the train slowly
	 * @param standardForward The forward direction of the train. Normally this is true as forward direction.
	 *                        But some trains have a reversed direction system.
	 */
	public void setParameters(String name, byte maxSpeed, byte normalSpeed, byte minSpeed, boolean standardForward) {
		this.setName(name);
		this.setMaxSpeed(maxSpeed);
		this.setNormalSpeed(normalSpeed);
		this.setMinSpeed(minSpeed);
		this.setStandardForward(standardForward);
	}

	public void setName(String name) {
		this.name = name;
	}

	private void setSpeed(byte speed) {
		if (speed > this.maxSpeed || speed < 0)
			return;
		this.speed = speed;
	}

	private void setActualSpeed(byte actualSpeed) {
		this.actualSpeed = actualSpeed;

		try {
			new SpeedMessage(this.getSlot(), this.actualSpeed).send();
		} catch (Exception e1) {
			Ref.LOGGER.severe("Failed to set speed, please try manually!");
		}
	}

	public void setNormalSpeed(byte normalSpeed) {
		this.normalSpeed = normalSpeed;
	}

	public void setMinSpeed(byte brakeSpeed) {
		this.minSpeed = brakeSpeed;
	}

	public void setMaxSpeed(byte maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	/**
	 * Sets this trains position to a sensor
	 * @param position The sensors address
	 */
	public void setPosition(int position) {
		this.actualPosition = position;
		LocoNet.getRailroad().getNodeByAddress(position).reservated = this;
	}

	/**
	 * Sets this trains last position to a sensor
	 * @param lposition The sensors address
	 */
	public void setLastPosition(int lposition) {
		this.lastPosition = lposition;
	}

	public void setStandardForward(boolean standardForward) {
		this.standardForward = standardForward;
	}

	public void setForward(boolean forward) {
		this.forward = forward;
	}

	/* Boolean returns */

	public boolean standardForward() {
		return standardForward;
	}

	/* Getter Methods */

	public String getName() {
		return this.name;
	}

	public byte getMinSpeed() {
		return minSpeed;
	}

	public byte getNormalSpeed() {
		return normalSpeed;
	}

	public byte getMaxSpeed() {
		return maxSpeed;
	}

	public byte getActualSpeed() {
		return actualSpeed;
	}

	public byte getAddress() {
		return this.address;
	}

	public byte getSlot() {
		return this.slot;
	}

	public int getActualPosition() {
		return this.actualPosition;
	}

	public int getLastPosition() {
		return this.lastPosition;
	}

	/* Simple speed controls */

	/**
	 * Set the speed of the train to his normal speed.
	 */
	public void applyNormalSpeed() {
		this.setActualSpeed(this.normalSpeed);
	}

	/**
	 * Set the speed of the train to his maximal speed.
	 */
	public void applyMaxSpeed() {
		this.setActualSpeed(this.maxSpeed);
	}

	/**
	 * Set the speed of the train to his minimal speed.
	 */
	public void applyBreakSpeed() {
		this.setActualSpeed(this.minSpeed);
	}

	/**
	 * Stops the train. (Sets his speed to 0)
	 *
	 * @implNote The train will stop slowly, not immediately
	 */
	public void stopTrain() {
		this.setActualSpeed((byte) 0);
	}

	/**
	 * Stops the train at the position where he actually is.
	 */
	public void stopTrainImmediately() {
		this.setActualSpeed((byte) -1);
	}

	/* Methods for the automatic driving process */

	public void trainEnter(int nodeAddress) {
		Ref.LOGGER.info("Train: " + this.getAddress());

		Railroad.Section position = LocoNet.getRailroad().getNodeByAddress(actualPosition);

		if(this.way != null && (this.way.containsKey(position) || (this.startWay != -1 && this.startPos != -1))) {
			if (this.actualPosition != nodeAddress && ((position.address == this.startPos &&
					position.nodeConnections.get(this.startWay).to == nodeAddress) ||
					(this.startWay == -1 && this.startPos == -1 &&
							position.nodeConnections.get(this.way.get(position)).to == nodeAddress))) {
				LocoNet.getRailroad().getNodeByAddress(this.lastPosition).reservated = null;

				this.lastPosition = this.actualPosition;
				this.actualPosition = nodeAddress;

				LocoNet.getRailroad().getNodeByAddress(nodeAddress).left = false;

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
					this.applyBreakSpeed();

					this.stopNext = true;

					if (node != null && way != null && way.containsKey(node))
						this.useConnection = way.get(node);

					if(LocoNet.getRailroad().getNodeByAddress(this.lastPosition).left) {
						this.stopTrain();
						this.stopNext = false;
						way = null;
						destination = -1;
						waiting = 10;
						LocoNet.getRailroad().getNodeByAddress(this.lastPosition).left = false;
						LocoNet.getRailroad().getNodeByAddress(this.lastPosition).reservated = null;
					}
				}

				this.reservateNext();
			} else if(this.actualPosition == nodeAddress && (position.reservated == null || position.left)) {
				position.reservated = this;
				position.left = false;
			}
		}
	}

	public void trainLeft(int nodeAddress) {
		if(this.stopNext && nodeAddress == this.lastPosition) {
			this.stopTrain();
			this.stopNext = false;
			way = null;
			destination = -1;
			waiting = 10;
		}
		if(nodeAddress == this.lastPosition && LocoNet.getRailroad().getNodeByAddress(this.lastPosition).reservated != null &&
				LocoNet.getRailroad().getNodeByAddress(this.lastPosition).reservated.equals(this)) {
			Ref.LOGGER.info("Train " + this.getAddress() + " left sensor " + nodeAddress);

			LocoNet.getRailroad().getNodeByAddress(lastPosition).reservated = null;
		}

		if(nodeAddress == this.actualPosition) {
			LocoNet.getRailroad().getNodeByAddress(nodeAddress).left = true;
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
					this.applyMaxSpeed();
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

				this.applyNormalSpeed();
			}
		}

		if(way == null)
			destination = -1;

		if(waiting > 0) {
			waiting--;
		} else if(waiting < 0)
			waiting = 0;
	}





	public void reservateNext() {
		if(way == null)
			return;

		Railroad r = LocoNet.getRailroad();

		Railroad.Section node = r.getNodeByAddress(actualPosition);

		if(node.reservated != this && node.reservated != null) {
			this.stopTrain();
			node.reservated.stopTrain();

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
				this.applyBreakSpeed();

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
					this.applyBreakSpeed();
					this.way = null;
					this.destination = -1;
				}
			}
		}
	}

	@Override
	public String toString() {
		return "Train{" +
				"address=" + address +
				", slot=" + slot +
				", minSpeed=" + minSpeed +
				", normalSpeed=" + normalSpeed +
				", maxSpeed=" + maxSpeed +
				'}';
	}

	@Override
	public boolean equals(Object train) {
		if (this == train ||
				(train != null && ((train.getClass().equals(Byte.class) && (byte)train == this.address) ||
						(train.getClass().equals(String.class) && ((String)train).equals(this.name))))) return true;
		if (train == null || getClass() != train.getClass()) return false;
		return ((Train)train).getAddress() == this.getAddress() &&
				((Train)train).getSlot() == this.getSlot();
	}

	@Override
	public int compareTo(Train train) {
		return this.getAddress() - train.getAddress();
	}
}
