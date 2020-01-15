package de.noisruker.client;

import java.io.IOException;
import java.util.logging.Level;

import de.noisruker.client.gui.GUIClient;
import de.noisruker.client.gui.GUIConnect;
import de.noisruker.common.ChatMessage;
import de.noisruker.common.messages.AbstractMessage;
import de.noisruker.common.messages.PasswordRequest;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketSender;
import de.noisruker.net.datapackets.DatapacketType;
import de.noisruker.net.datapackets.NetEvent;
import de.noisruker.net.datapackets.NetEventHandler;
import de.noisruker.util.Ref;

public class Events {

	@NetEventHandler(type = DatapacketType.PASSWORD_REQUEST)
	public static void onPasswordRequest(NetEvent netEvent) {
		Ref.LOGGER.info("p");
		PasswordRequest request = (PasswordRequest) netEvent.getDatapacket().getValue();
		DatapacketSender sender = netEvent.getSender();

		try {
			Client.getConnectionHandler().sendDatapacket(new Datapacket(DatapacketType.PASSWORD_ANSWER,
					new ClientPassword(GUIConnect.password, GUIConnect.name)));
		} catch (IOException e) {
			Ref.LOGGER.log(Level.SEVERE, "", e);
		}
	}

	@NetEventHandler(type = DatapacketType.SERVER_SEND_CHAT_MESSAGE)
	public static void handleChatMessage(NetEvent netEvent) {
		Ref.LOGGER.info(((ChatMessage) netEvent.getDatapacket().getValue()).getFormatted());
		GUIClient.getInstance().messages.appendText(((ChatMessage) netEvent.getDatapacket().getValue()).getFormatted());
	}

	@NetEventHandler(type = DatapacketType.SERVER_SEND_MESSAGE)
	public static void handleLocoNetMessage(NetEvent netEvent) {
		Ref.LOGGER
				.info(((AbstractMessage) netEvent.getDatapacket().getValue()).toLocoNetMessage().getType().toString());

		GUIClient.getInstance().messages.appendText(
				((AbstractMessage) netEvent.getDatapacket().getValue()).toLocoNetMessage().getType().toString());
	}

	@NetEventHandler(type = DatapacketType.START_CLIENT_INTERFACE)
	public static void startGUI(NetEvent netEvent) {
		Ref.LOGGER.info("g");

		GUIConnect.canOpen = true;
	}

}
