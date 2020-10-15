package de.noisruker.main;

import static de.noisruker.util.Ref.LOGGER;
import static de.noisruker.util.Ref.PROJECT_NAME;
import static de.noisruker.util.Ref.VERSION;

import java.io.Console;
import java.io.IOException;
import java.util.logging.Level;

import de.noisruker.common.Railroad;
import de.noisruker.common.Sensor;
import de.noisruker.common.Train;
import de.noisruker.common.messages.AbstractMessage;
import de.noisruker.common.messages.ChatMessage;
import de.noisruker.common.messages.DirectionMessage;
import de.noisruker.common.messages.SpeedMessage;
import de.noisruker.common.messages.SwitchMessage;
import de.noisruker.net.Side;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketType;
import de.noisruker.server.Action;
import de.noisruker.server.ClientHandler;
import de.noisruker.server.ClientHandler.PermissionLevel;
import de.noisruker.server.Server;
import de.noisruker.server.loconet.LocoNet;
import de.noisruker.server.loconet.LocoNetConnection.PortNotOpenException;
import de.noisruker.server.loconet.messages.LocoNetMessage;
import de.noisruker.server.loconet.messages.MessageType;
import de.noisruker.util.Ref;
import jssc.SerialPortException;

public class Main {

	public static void main(String[] args) {
		Ref.side = Side.SERVER;

		LOGGER.info("Starte: " + PROJECT_NAME + " | Version: " + VERSION);
		new Thread(() -> {
			try {
				Ref.password = "Standart";

				Server.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();

		new Thread(() -> {
			Console scanner = System.console();

			Ref.LOGGER.info("Password:");

			Ref.password = new String(System.console().readPassword());

			Ref.LOGGER.info("Aviable Ports:");

			for (String s : jssc.SerialPortList.getPortNames())
				Ref.LOGGER.info(s);

			Ref.LOGGER.info("Connection Port:");

			try {
				LocoNet.getInstance().start(scanner.readLine());
			} catch (SerialPortException e1) {
				e1.printStackTrace();
			}

			while (true) {
				try {
					String command = scanner.readLine();

					switch (command) {
					case "help":
						Ref.LOGGER.info("Aviable Commands:");

						Ref.LOGGER.info("help");
						Ref.LOGGER.info("op");
						Ref.LOGGER.info("kick");
						Ref.LOGGER.info("addTrain");
						Ref.LOGGER.info("speed");
						Ref.LOGGER.info("switch");
						Ref.LOGGER.info("sensors");
						Ref.LOGGER.info("direction");
						Ref.LOGGER.info("message");
						Ref.LOGGER.info("drive");
						Ref.LOGGER.info("record");
						Ref.LOGGER.info("addEvent");
						Ref.LOGGER.info("clear");
						Ref.LOGGER.info("actions");
						Ref.LOGGER.info("auto");
						Ref.LOGGER.info("exitAuto");

						break;

					case "sensors":
						Ref.LOGGER.info("Sensors:");
						for (Sensor s : LocoNet.getInstance().getSensors())
							Ref.LOGGER.info("Address: " + Integer.toString(s.getAddress()) + "; Slot: "
									+ Boolean.toString(s.getState()));
						break;
					case "op":
						Ref.LOGGER.info("Players:");

						for (ClientHandler ch : Server.getClientHandlers())
							Ref.LOGGER.info(ch != null ? ch.getName() : "");

						Ref.LOGGER.info("Type Playername:");

						String name = scanner.readLine();

						Ref.LOGGER.info("Type Permissionlevel:");

						int level = Integer.parseInt(scanner.readLine());

						for (ClientHandler ch : Server.getClientHandlers()) {
							if (ch == null)
								continue;

							if (ch.getName().equals(name)) {
								ch.setPermissionLevel(PermissionLevel.getByLevel(level));
							}
						}

						break;
					case "kick":
						Ref.LOGGER.info("Players:");

						for (ClientHandler ch : Server.getClientHandlers())
							Ref.LOGGER.info(ch != null ? ch.getName() : "");

						Ref.LOGGER.info("Type Playername:");

						String name1 = scanner.readLine();

						for (ClientHandler ch : Server.getClientHandlers()) {
							if (ch.getName().equals(name1)) {
								Server.removeClient(ch);
							}
						}

						break;
					case "addTrain":
						Ref.LOGGER.info("Type Address:");

						try {
							byte address = Byte.parseByte(scanner.readLine());
							new LocoNetMessage(MessageType.OPC_LOCO_ADR, (byte) 0, address).send();
						} catch (Exception | PortNotOpenException e1) {
							Ref.LOGGER.severe("Failed to add Train, please try again!");
						}

						break;
					case "editTrain":
						Ref.LOGGER.info("Trains:");
						for (Train t : LocoNet.getInstance().getTrains())
							Ref.LOGGER.info("Address: " + Byte.toString(t.getAddress()) + "; Slot: "
									+ Byte.toString(t.getSlot()));

						Ref.LOGGER.info("Type address:");
						byte address = Byte.parseByte(scanner.readLine());

						Ref.LOGGER.info("Type position:");
						int position = Integer.parseInt(scanner.readLine());

						Ref.LOGGER.info("Type last position:");
						int lposition = Integer.parseInt(scanner.readLine());

						for(Train t: LocoNet.getInstance().getTrains()) {
							if (t.getAddress() == address) {
								t.setPosition(position);
								t.setLastPosition(lposition);
							}
						}

						break;
					case "speed":
						Ref.LOGGER.info("Trains:");
						for (Train t : LocoNet.getInstance().getTrains())
							Ref.LOGGER.info("Address: " + Byte.toString(t.getAddress()) + "; Slot: "
									+ Byte.toString(t.getSlot()));
						Ref.LOGGER.info("Type Slot:");

						byte slot = Byte.parseByte(scanner.readLine());
						Ref.LOGGER.info("Type Speed: (0 - 125)");

						try {
							byte speed = Byte.parseByte(scanner.readLine());
							new SpeedMessage(slot, speed).send();
						} catch (Exception e1) {
							Ref.LOGGER.severe("Failed to set speed, please try again!");
						}

						break;
					case "direction":
						Ref.LOGGER.info("Trains:");
						for (Train t : LocoNet.getInstance().getTrains())
							Ref.LOGGER.info("Address: " + Byte.toString(t.getAddress()) + "; Slot: "
									+ Byte.toString(t.getSlot()));
						Ref.LOGGER.info("Type Slot:");

						byte train_slot = Byte.parseByte(scanner.readLine());
						Ref.LOGGER.info("Type Foreward: (boolean)");

						try {
							boolean direction = Boolean.parseBoolean(scanner.readLine());
							new DirectionMessage(train_slot, direction).send();
						} catch (Exception e1) {
							Ref.LOGGER.severe("Failed to set speed, please try again!");
						}

						break;
					case "drive":
						Ref.LOGGER.info("Type drive automatic: (boolean)");

						try {
							LocoNet.drive = Boolean.parseBoolean(scanner.readLine());
						} catch (Exception e1) {
							Ref.LOGGER.severe("Failed to set speed, please try again!");
						}

						break;
					case "record":
						Ref.LOGGER.info("Type record: (boolean)");

						try {
							LocoNet.record = Boolean.parseBoolean(scanner.readLine());
						} catch (Exception e1) {
							Ref.LOGGER.severe("Failed to set speed, please try again!");
						}

						break;
					case "actions":

						try {
							for (Action e : LocoNet.getInstance().actions) {
								Ref.LOGGER.info(e.toString());
							}
						} catch (Exception e1) {
							Ref.LOGGER.severe("Failed to set speed, please try again!");
						}

						break;
					case "addAction":

						Ref.LOGGER.info("Not implemented yet!");

						break;
					case "addEvent":

						try {
							AbstractMessage message = null;

							Ref.LOGGER.info("Choose Message Type:");
							Ref.LOGGER.info("1 - SpeedMessage");
							Ref.LOGGER.info("2 - SwitchMessage");

							switch (Integer.valueOf(scanner.readLine())) {
							case 1:
								Ref.LOGGER.info("Trains:");
								for (Train t : LocoNet.getInstance().getTrains())
									Ref.LOGGER.info("Address: " + Byte.toString(t.getAddress()) + "; Slot: "
											+ Byte.toString(t.getSlot()));
								Ref.LOGGER.info("Type Slot:");

								byte slot1 = Byte.parseByte(scanner.readLine());
								Ref.LOGGER.info("Type Speed: (0 - 125)");

								try {
									byte speed = Byte.parseByte(scanner.readLine());
									message = new SpeedMessage(slot1, speed);
								} catch (Exception e1) {
									Ref.LOGGER.severe("Failed to set speed, please try again!");
								}
								break;
							case 2:
								Ref.LOGGER.info("Type Address:");

								byte address1 = Byte.parseByte(scanner.readLine());
								Ref.LOGGER.info("Type Direction: (boolean)");

								boolean state = Boolean.parseBoolean(scanner.readLine());

								message = new SwitchMessage(address1, state);

								break;
							default:
								message = null;
								break;
							}

							LocoNet.getInstance().actions.get(LocoNet.getInstance().actions.size() - 1)
									.addEvent(message);

						} catch (Exception e1) {
							Ref.LOGGER.severe("Failed to set speed, please try again!");
						}

						break;
					case "clear":

						LocoNet.getInstance().actions.clear();

						break;
					case "switch":

						Ref.LOGGER.info("Type Address:");

						byte address1 = Byte.parseByte(scanner.readLine());
						Ref.LOGGER.info("Type Direction: (boolean)");

						boolean state = Boolean.parseBoolean(scanner.readLine());

						try {
							new SwitchMessage(address1, state).send();
						} catch (IOException e1) {
							e1.printStackTrace();
						}

						break;

					case "auto":
						LocoNet.getInstance().activateDriveAuto();

						break;

					case "exitAuto":
						LocoNet.getInstance().stopAutoDrive();
						break;

					case "message":
						Ref.LOGGER.info("Type Message:");

						String message = scanner.readLine();

						ChatMessage m = new ChatMessage(message);
						m.setName("SERVER");
						m.setLevel("HOST");

						for (ClientHandler ch : Server.getClientHandlers())
							try {
								ch.sendDatapacket(new Datapacket(DatapacketType.SERVER_SEND_CHAT_MESSAGE, m));
							} catch (IOException e) {
								Ref.LOGGER.log(Level.SEVERE, "", e);
							}

						break;
					case "stop":
						try {
							LocoNet.getInstance().stop();
						} catch (SerialPortException e1) {
							Ref.LOGGER.info("Cannot stop regular");
							System.exit(1);
						}
						System.exit(0);
						break;
					case "trains":
						for(Train t: LocoNet.getInstance().getTrains()) {
							LOGGER.info("Train " + t.getAddress() + "[" + t.actualPosition + "|" + t.lastPosition + "]");
						}
						break;
					case "sensor":
						for(Railroad.Section r: LocoNet.getRailroad().nodes) {
							LOGGER.info("Sensor " + r.getAddress() + "[" + (r.getReservated() == null ? -1 : r.getReservated().getAddress()) + "]");
						}
						break;
					default:
						Ref.LOGGER.info("To get help type \"help\"");
						break;
					}

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					Ref.LOGGER.severe("Invalid action, please try again!");
				}
			}
		}).start();
	}

}
