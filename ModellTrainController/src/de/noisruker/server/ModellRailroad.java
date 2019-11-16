package de.noisruker.server;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import de.noisruker.common.TrainManager;
import de.noisruker.util.Ref;
import jssc.SerialPort;
import jssc.SerialPortException;

public class ModellRailroad {

	private TrainManager trainManager;
	
	/**
	 * Der serielle Port, über den die Modellbahn angesprochen wird.
	 */
	private static SerialPort modellRailroadPort;
	
	/**
	 * Die laufende Instanz der Klasse ModellRailroad.
	 */
	private static ModellRailroad instance;
	
	/**
	 * @return Gibt die aktuelle Instanz der Klasse Railroad zurück.
	 */
	public static ModellRailroad getInstance() {
		return ModellRailroad.instance != null ? ModellRailroad.instance : (ModellRailroad.instance = new ModellRailroad());
	}
	
	private ModellRailroad() {
		ModellRailroad.modellRailroadPort = new SerialPort("COM3");
		try {
			ModellRailroad.modellRailroadPort.openPort();
		} catch (SerialPortException e) {
			Ref.LOGGER.log(Level.SEVERE, "Unnable to open the connection to the TwinCenter", e.getCause());
		}
		this.trainManager = new TrainManager();
	}
	
	public TrainManager getTrainManager() {
		return this.trainManager;
	}
	
	public void close() {
		try {
			ModellRailroad.modellRailroadPort.closePort();
		} catch (SerialPortException e) {
			Ref.LOGGER.log(Level.SEVERE, "Unnable to close the connection to the TwinCenter", e.getCause());
		}
		
		ModellRailroad.instance = null;
	}
	
	public void setSpeedOfTrain(byte address, byte speed, boolean foreward) throws SerialPortException {
		String message;
		if(foreward)
			message = "v" + Short.toString(address) + "v" + Short.toString(speed);
		else
			message = "r" + Short.toString(address) + "r" + Short.toString(speed);
		
		ModellRailroad.modellRailroadPort.writeBytes(message.getBytes(StandardCharsets.UTF_8));
	}
	
}
