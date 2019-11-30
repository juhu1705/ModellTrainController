package de.noisruker.common;

import de.noisruker.server.ModellRailroad;
import de.noisruker.util.Ref;
import jssc.SerialPortException;

import java.io.Serializable;

public class Train implements Serializable {

	/**
	 * Die universelle Serial-Version-UID
	 */
	private static final long serialVersionUID = Ref.UNIVERSAL_SERIAL_VERSION_UID;
	
	
	/**
	 * Die eindeutige Addresse unter der ein Zug eingespeichert ist.
	 */
	private byte address;
	private byte speed, maxSpeed, normalSpeed, minSpeed, actualSpeed;
	private boolean foreward, standartForeward;

	/**
	 * Generiert einen neuen {@link Train Zug} mit der entsprechenden addresse. Es kann nur einen Zug mit einer speuifischen Addresse geben.
	 *
	 * @param address Adresse des Zuges
	 */
	Train(byte address) {
		this.address = address;
		this.setMaxSpeed((byte) 15);
		this.setNormalSpeed((byte) 8);
		this.setMinSpeed((byte) 4);
		this.setStandartForeward(true);
		this.setSpeed((byte) 0);
		this.setActualSpeed(this.speed);
	}
	
	Train(byte address, byte maxSpeed, byte normalSpeed, byte minSpeed, boolean standartForeward) {
		this.address = address;
		this.setMaxSpeed(maxSpeed);
		this.setNormalSpeed(normalSpeed);
		this.setMinSpeed(minSpeed);
		this.setStandartForeward(standartForeward);
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

	public void sendTrainDataToModellRailroad() throws SerialPortException {
		ModellRailroad.getInstance().setSpeedOfTrain(this.address, this.speed, this.foreward);
	}

	public void stop() throws SerialPortException {
		this.setSpeed((byte) 0);

		ModellRailroad.getInstance().setSpeedOfTrain(this.address, (byte) 0, this.foreward);
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
	
	public void update() {
		
	}


}
