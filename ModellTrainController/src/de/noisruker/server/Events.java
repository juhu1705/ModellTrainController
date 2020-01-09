package de.noisruker.server;

import java.io.IOException;
import java.util.logging.Level;

import de.noisruker.client.ClientPassword;
import de.noisruker.common.ChatMessage;
import de.noisruker.common.messages.SpeedMessage;
import de.noisruker.common.messages.SwitchMessage;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketType;
import de.noisruker.net.datapackets.DatapacketVoid;
import de.noisruker.net.datapackets.NetEvent;
import de.noisruker.net.datapackets.NetEventHandler;
import de.noisruker.util.Ref;
import javafx.application.Platform;

public class Events {

//	@NetEventHandler(type = DatapacketType.SEND_COMMAND)
//	public static void recieveCommand(NetEvent netEvent) {
//		Ref.LOGGER.info("r");
//		try {
//			ModellRailroad.getInstance().sendCommand((String) netEvent.getDatapacket().getValue(),
//					netEvent.getSender());
//		} catch (SerialPortException e) {
//			e.printStackTrace();
//		}
//	}

	@NetEventHandler(type = DatapacketType.CLIENT_SEND_CHAT_MESSAGE)
	public static void clientMessage(NetEvent netEvent) {
		Ref.LOGGER.info("Message");
		ChatMessage m = ((ChatMessage) netEvent.getDatapacket().getValue())
				.setName(((ClientHandler) netEvent.getSender()).getName())
				.setLevel(((ClientHandler) netEvent.getSender()).getPermissionLevel().toString());

		Ref.LOGGER.fine(m.getFormatted());

		for (ClientHandler ch : Server.getClientHandlers())
			try {
				ch.sendDatapacket(new Datapacket(DatapacketType.SERVER_SEND_CHAT_MESSAGE, m));
			} catch (IOException e) {
				Ref.LOGGER.log(Level.SEVERE, "", e);
			}

	}

	@NetEventHandler(type = DatapacketType.SEND_SPEED_MESSAGE)
	public static void clientSendSpeedMessage(NetEvent netEvent) {
		try {
			((SpeedMessage) (netEvent.getDatapacket().getValue())).send();
		} catch (IOException e) {
			Ref.LOGGER.log(Level.SEVERE, "", e);
		}
	}

	@NetEventHandler(type = DatapacketType.SEND_SWITCH_MESSAGE)
	public static void clientSendSwitchMessage(NetEvent netEvent) {
		try {
			((SwitchMessage) (netEvent.getDatapacket().getValue())).send();
		} catch (IOException e) {
			Ref.LOGGER.log(Level.SEVERE, "", e);
		}
	}

	@NetEventHandler(type = DatapacketType.PASSWORD_ANSWER)
	public static void passwordRequest(NetEvent netEvent) {
		Ref.LOGGER.info("r");

		ClientPassword password = (ClientPassword) netEvent.getDatapacket().getValue();
		ClientHandler sender = (ClientHandler) netEvent.getSender();

		if (Ref.password.equals(password.getPassword())) {
			Server.nonRegisteredClientHandler.clear();
			for (ClientHandler ch : Server.getClientHandlers()) {
				if (sender.equals(ch)) {
					ch.setName(password.getName());
					try {
						ch.sendDatapacket(
								new Datapacket(DatapacketType.START_CLIENT_INTERFACE, DatapacketVoid.getDummy()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			Platform.runLater(() -> {
				// GUIServer.getInstance().players.setItems(FXCollections.observableArrayList(Server.getClientHandlers()));
			});

		} else
			Server.removeClient(sender);
	}

}
