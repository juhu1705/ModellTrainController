package de.noisruker.server;

import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketSender;
import de.noisruker.net.datapackets.DatapacketType;
import de.noisruker.util.Ref;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;

public class ModellRailroad implements Runnable {

	/**
	 * Der serielle Port, über den die Modellbahn angesprochen wird.
	 */
	private static SerialPort modellRailroadPort;
	/**
	 * Die laufende Instanz der Klasse ModellRailroad.
	 */
	private static ModellRailroad instance;
	private boolean active = true;
	private ArrayList<CommandMessage> messages = new ArrayList<>();
	private CommandMessage actualMessage = null;
	private boolean recievedAnswer = false;

	private ModellRailroad() {
		ModellRailroad.modellRailroadPort = new SerialPort("COM3");
		try {
			ModellRailroad.modellRailroadPort.openPort();
			ModellRailroad.modellRailroadPort.setParams(9600, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_2,
					SerialPort.PARITY_NONE);
		} catch (SerialPortException e) {
			Ref.LOGGER.log(Level.SEVERE, "Unnable to open the connection to the TwinCenter", e.getCause());
		}
	}

	/**
	 * @return Gibt die aktuelle Instanz der Klasse Railroad zurück.
	 */
	public static ModellRailroad getInstance() {
		return ModellRailroad.instance != null ? ModellRailroad.instance : (ModellRailroad.instance = new ModellRailroad());
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
		if (foreward)
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

					Ref.LOGGER.info(message);

					if (this.actualMessage.getSender() != null)
						((ClientHandler) this.actualMessage.getSender()).sendDatapacket(new Datapacket(DatapacketType.HAS_FUNKTION, Boolean.valueOf(message.equalsIgnoreCase(this.actualMessage.getCommand()))));
					this.recievedAnswer = true;
				} catch (SerialPortException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (SerialPortException e1) {
			e1.printStackTrace();
		}

		while (active) {
			if (!this.messages.isEmpty()) {
				this.recievedAnswer = false;
				this.actualMessage = this.messages.remove(0);
				if (this.actualMessage == null)
					continue;
				Ref.LOGGER.info(this.actualMessage.getCommand());
				try {
					ModellRailroad.modellRailroadPort.writeBytes(this.actualMessage.getCommand().getBytes(StandardCharsets.UTF_8));
				} catch (SerialPortException e) {
					e.printStackTrace();
				}
				while (!this.recievedAnswer) {
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
