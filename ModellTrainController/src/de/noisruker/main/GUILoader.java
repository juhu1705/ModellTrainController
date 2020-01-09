package de.noisruker.main;

import static de.noisruker.util.Ref.LOGGER;
import static de.noisruker.util.Ref.PROJECT_NAME;
import static de.noisruker.util.Ref.VERSION;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;

import de.noisruker.common.ChatMessage;
import de.noisruker.net.Side;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketType;
import de.noisruker.server.ClientHandler;
import de.noisruker.server.ClientHandler.PermissionLevel;
import de.noisruker.server.Server;
import de.noisruker.server.loconet.LocoNet;
import de.noisruker.util.Ref;
import jssc.SerialPortException;

public class GUILoader {

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
			Scanner scanner = new Scanner(System.in);

			Ref.LOGGER.info("Password:");

			Ref.password = scanner.next();

			Ref.LOGGER.info("Aviable Ports:");

			for (String s : jssc.SerialPortList.getPortNames())
				Ref.LOGGER.info(s);

			Ref.LOGGER.info("Connection Port:");

			try {
				LocoNet.getInstance().start(scanner.next());
			} catch (SerialPortException e1) {
				e1.printStackTrace();
			}

			while (true) {
				String command = scanner.next();

				switch (command) {
				case "help":
					Ref.LOGGER.info("Aviable Commands:");

					Ref.LOGGER.info("help");
					Ref.LOGGER.info("op");
					Ref.LOGGER.info("kick");
					Ref.LOGGER.info("addTrain");
					Ref.LOGGER.info("speed");
					Ref.LOGGER.info("switch");
					Ref.LOGGER.info("message");

					break;

				case "op":
					Ref.LOGGER.info("Type Playername:");

					String name = scanner.next();

					Ref.LOGGER.info("Type Permissionlevel:");

					int level = Integer.parseInt(scanner.next());

					for (ClientHandler ch : Server.getClientHandlers()) {
						if (ch.getName().equals(name)) {
							ch.setPermissionLevel(PermissionLevel.getByLevel(level));
						}
					}

					break;
				case "kick":

					Ref.LOGGER.info("Type Playername:");

					String name1 = scanner.next();

					for (ClientHandler ch : Server.getClientHandlers()) {
						if (ch.getName().equals(name1)) {
							try {
								ch.kick();
							} catch (IOException e) {

							}
						}
					}

					break;
				case "addTrain":
					Ref.LOGGER.info("Type Address:");

					int address = Integer.parseInt(scanner.next());

					break;
				case "speed":
					Ref.LOGGER.info("NO FUNKTION");

					break;
				case "switch":
					Ref.LOGGER.info("NO FUNKTION");

					break;

				case "message":
					Ref.LOGGER.info("Type Message:");

					String message = scanner.next();

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
				default:
					Ref.LOGGER.info("To get help type \"help\"");
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

}
