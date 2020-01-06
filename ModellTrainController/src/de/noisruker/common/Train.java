package de.noisruker.common;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;

import de.noisruker.common.messages.SpeedMessage;
import de.noisruker.util.Ref;
import jssc.SerialPortException;

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
	private boolean foreward, standartForeward;

	private Track next, actual, previous;
	private LinkedList<Track> stations = new LinkedList<>();

	/**
	 * Generiert einen neuen {@link Train Zug} mit der entsprechenden addresse. Es
	 * kann nur einen Zug mit einer speuifischen Addresse geben.
	 *
	 * @param address Adresse des Zuges
	 */
	public Train(byte address) {
		this.address = address;
		this.setMaxSpeed((byte) 15);
		this.setNormalSpeed((byte) 8);
		this.setMinSpeed((byte) 4);
		this.setStandartForeward(true);
		this.setSpeed((byte) 0);
		this.setActualSpeed(this.speed);
	}

	public Train(byte address, byte slot, byte speed) {
		this.address = address;
		this.slot = slot;
		this.setMaxSpeed((byte) 15);
		this.setNormalSpeed((byte) 8);
		this.setMinSpeed((byte) 4);
		this.setStandartForeward(true);
		this.setSpeed(speed);
		this.setActualSpeed(this.speed);
		this.startUpdater();
	}

	public Train(byte address, byte maxSpeed, byte normalSpeed, byte minSpeed, boolean standartForeward) {
		this.address = address;
		this.setMaxSpeed(maxSpeed);
		this.setNormalSpeed(normalSpeed);
		this.setMinSpeed(minSpeed);
		this.setStandartForeward(standartForeward);
	}

	private void startUpdater() {
		new Thread(() -> {
			while (true) {
				if (this.actualSpeed < this.speed) {
					this.speed--;
					try {
						new SpeedMessage(this.slot, this.speed).send();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (this.actualSpeed > this.speed) {
					this.speed++;
					try {
						new SpeedMessage(this.slot, this.speed).send();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void setTracks(Track next, Track actual, Track previous) {
		this.next = next;
		this.actual = actual;
		this.previous = previous;
	}

	public Track getNext() {
		return this.next;
	}

	public Track getPrevious() {
		return this.previous;
	}

	public Track getActual() {
		return this.actual;
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
		this.setSpeed(this.normalSpeed);
	}

	public void setMaxSpeed() {
		this.setSpeed(this.maxSpeed);
	}

	public void setBreakSpeed() {
		this.setSpeed(this.minSpeed);
	}

	@Deprecated
	public void sendTrainDataToModellRailroad() throws SerialPortException {

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
	}

	public byte getSlot() {
		return this.slot;
	}

	public void setNext() {
		Ref.LOGGER.info(this.stations.toString());

		Track nnext = this.stations.poll();
		this.stations.addLast(nnext);
		this.previous = this.actual;
		this.actual = this.next;

		this.previous.setTrain(null);
		this.actual.setTrain(this);

		this.next = this.actual.getNext(this.actual, nnext, this.previous);

		Ref.LOGGER.config(this.previous.toString() + "" + this.actual.toString() + "" + this.next.toString() + "");
		Ref.LOGGER.info(this.stations.toString());

	}

	public void setRoad(Track... tracks) {
		for (Track t : tracks) {
			this.stations.addLast(t);
		}
	}

}
