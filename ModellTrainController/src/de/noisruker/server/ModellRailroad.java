package de.noisruker.server;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;

import de.noisruker.net.datapackets.DatapacketSender;
import de.noisruker.util.Ref;
import jssc.SerialPort;
import jssc.SerialPortException;

@Deprecated
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
	public ArrayList<ByteMessage> m = new ArrayList<>();
	private CommandMessage actualMessage = null;
	private boolean recievedAnswer = false;

	private ModellRailroad() {
		ModellRailroad.modellRailroadPort = new SerialPort("COM3");
		try {
			ModellRailroad.modellRailroadPort.openPort();
			ModellRailroad.modellRailroadPort.setParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_2,
					SerialPort.PARITY_NONE);
		} catch (SerialPortException e) {
			Ref.LOGGER.log(Level.SEVERE, "Unnable to open the connection to the TwinCenter", e.getCause());
		}
	}

	/**
	 * @return Gibt die aktuelle Instanz der Klasse Railroad zurück.
	 */
	public static ModellRailroad getInstance() {
		return ModellRailroad.instance != null ? ModellRailroad.instance
				: (ModellRailroad.instance = new ModellRailroad());
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
		this.m.add(new ByteMessage(this.addCheckSum((byte) -96, address, speed)));
	}

	@Override
	public void run() {
		try {
			ModellRailroad.modellRailroadPort.addEventListener(l -> {
				try {
					Thread.sleep(1000);

					byte[] message = ModellRailroad.modellRailroadPort.readBytes();

					//

					if (message == null)
						return;

					String s = "";

					byte bb = 0;

					for (int b : message) {
						s += b + "|";
						bb ^= b;
					}

					Ref.LOGGER.config(s);
					Ref.LOGGER.config(bb + "|" + (byte) 0xFF);

//					if (this.actualMessage.getSender() != null)
//						((ClientHandler) this.actualMessage.getSender())
//								.sendDatapacket(new Datapacket(DatapacketType.HAS_FUNKTION,
//										Boolean.valueOf(message.equalsIgnoreCase(this.actualMessage.getCommand()))));
					this.recievedAnswer = true;
				} catch (SerialPortException | InterruptedException e) {
					e.printStackTrace();
				}
			});
		} catch (SerialPortException e1) {
			e1.printStackTrace();
		}

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (active) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					if (!m.isEmpty()) {
						byte[] array = m.get(0).getBytes();

						for (byte i : array)
							Ref.LOGGER.config(i + "");

						try {
							ModellRailroad.modellRailroadPort.writeBytes(m.remove(0).getBytes());
						} catch (SerialPortException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		t.start();

		while (active) {
			if (!this.messages.isEmpty()) {
				this.recievedAnswer = false;
				this.actualMessage = this.messages.remove(0);
				if (this.actualMessage == null)
					continue;
				Ref.LOGGER.info(this.actualMessage.getCommand());
				try {
					ModellRailroad.modellRailroadPort
							.writeBytes(String
									.format("%040x",
											new BigInteger(1,
													this.actualMessage.getCommand().getBytes(StandardCharsets.UTF_8)))
									.getBytes());
				} catch (SerialPortException e) {
					e.printStackTrace();
				}
				int i = 0;
				while (!this.recievedAnswer) {
					try {
						Thread.sleep(1000);
						i++;
						if (i == 10)
							break;
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

	public byte getCheckSum(byte... bytes) {
		byte xoredbydes = 0;

		for (byte b : bytes)
			xoredbydes ^= b;

		return (byte) ~xoredbydes;
	}

	public byte[] addCheckSum(byte... bytes) {
		byte[] b = new byte[bytes.length + 1];
		for (int i = 0; i < bytes.length; i++)
			b[i] = bytes[i];

		b[b.length - 1] = this.getCheckSum(bytes);
		return b;
	}

}
