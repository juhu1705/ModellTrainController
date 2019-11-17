package de.noisruker.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;

import de.noisruker.common.TrainManager;
import de.noisruker.net.datapackets.DatapacketSender;
import de.noisruker.util.Ref;
import jssc.SerialPort;
import jssc.SerialPortException;

public class ModellRailroad implements Runnable {

	
	
	private TrainManager trainManager;
	private boolean active = true;
	
	private ArrayList<CommandMessage> messages = new ArrayList<>();
	
	private CommandMessage actualMessage = null;
	private boolean recievedAnswer = false;
	
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
		active = false;
		ModellRailroad.instance = null;
	}
	
	public void sendCommand(String command, DatapacketSender sender) throws SerialPortException {
		this.messages.add(new CommandMessage(command, sender));
	}
	
	
	
	public void setSpeedOfTrain(byte address, byte speed, boolean foreward) throws SerialPortException {
		String message;
		if(foreward)
			message = "v" + Short.toString(address) + "v" + Short.toString(speed);
		else
			message = "r" + Short.toString(address) + "r" + Short.toString(speed);
		
		ModellRailroad.modellRailroadPort.writeBytes(message.getBytes(StandardCharsets.UTF_8));
	}
	

	@Override
	public void run() {
		try {
			ModellRailroad.modellRailroadPort.addEventListener(l -> {
				try {
					String message = ModellRailroad.modellRailroadPort.readString();
					message.equalsIgnoreCase(this.actualMessage.getCommand());
						
					
						
				} catch (SerialPortException e) {
					e.printStackTrace();
				}
			});
		} catch (SerialPortException e1) {
			e1.printStackTrace();
		}
		
		while(active) {
			if(!this.messages.isEmpty()) {
				this.recievedAnswer = false;
				this.actualMessage = this.messages.remove(0);
				if(this.actualMessage == null)
					continue;
				try {
					ModellRailroad.modellRailroadPort.writeBytes(this.actualMessage.getCommand().getBytes(StandardCharsets.UTF_8));
				} catch (SerialPortException e) {
					e.printStackTrace();
				}
				while(!this.recievedAnswer) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}
	
}
