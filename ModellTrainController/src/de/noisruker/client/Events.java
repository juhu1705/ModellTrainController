package de.noisruker.client;

import de.noisruker.client.gui.GUIConnect;
import de.noisruker.net.datapackets.*;
import de.noisruker.server.PasswordRequest;
import de.noisruker.util.Ref;

import java.io.IOException;

public class Events {

	@NetEventHandler(type = DatapacketType.PASSWORD_REQUEST)
	public static void onPasswordRequest(NetEvent netEvent) {
		Ref.LOGGER.info("p");
		PasswordRequest request = (PasswordRequest) netEvent.getDatapacket().getValue();
		DatapacketSender sender = netEvent.getSender();

		try {
			Client.getConnectionHandler().sendDatapacket(new Datapacket(DatapacketType.PASSWORD_ANSWER, new ClientPassword(GUIConnect.password, GUIConnect.name)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@NetEventHandler(type = DatapacketType.START_CLIENT_INTERFACE)
	public static void startGUI(NetEvent netEvent) {
		Ref.LOGGER.info("g");

		GUIConnect.canOpen = true;
	}

}
