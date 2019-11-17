package de.noisruker.common;

import java.io.Serializable;

import de.noisruker.server.ModellRailroad;
import de.noisruker.util.Ref;
import jssc.SerialPortException;

public class Train implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = Ref.UNIVERSAL_SERIAL_VERSION_UID;
	/**
	 * Die eindeutige Addresse unter der ein Zug eingespeichert ist.
	 */
	private byte address;
	private byte speed;
	private boolean foreward;
	
	/**
	 * Generiert einen neuen {@link Train Zug} mit der entsprechenden addresse. Es kann nur einen Zug mit einer speuifischen Addresse geben.
	 * @param address
	 */
	Train(byte address) {
		this.address = address;
	}
	
	public void setForeward(boolean foreward) {
		this.foreward = foreward;
	}
	
	public boolean setSpeed(byte speed) {
		if(speed > 15 || speed < 0)
			return false;
		this.speed = speed;
		return true;
	}
	
	public void sendTrainDataToModellRailroad() throws SerialPortException {
		ModellRailroad.getInstance().setSpeedOfTrain(this.address, this.speed, this.foreward);
	}
	
	public void stop() throws SerialPortException {
		this.speed = 0;
		
		ModellRailroad.getInstance().setSpeedOfTrain(this.address, (byte) 0, this.foreward);
	}
	
	
}
