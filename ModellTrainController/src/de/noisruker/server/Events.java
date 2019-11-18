package de.noisruker.server;

import java.io.IOException;

import de.noisruker.client.ClientPassword;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketType;
import de.noisruker.net.datapackets.DatapacketVoid;
import de.noisruker.net.datapackets.NetEvent;
import de.noisruker.net.datapackets.NetEventHandler;
import de.noisruker.util.Ref;
import jssc.SerialPortException;

public class Events {

	@NetEventHandler(type = DatapacketType.SEND_COMMAND)
	public static void recieveCommand(NetEvent netEvent) {
		Ref.LOGGER.info("r");
		try {
			ModellRailroad.getInstance().sendCommand((String)netEvent.getDatapacket().getValue(), netEvent.getSender());
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}
	
	@NetEventHandler(type = DatapacketType.PASSWORD_ANSWER)
	public static void passwordRequest(NetEvent netEvent)	{
		Ref.LOGGER.info("r");
		
		ClientPassword password = (ClientPassword) netEvent.getDatapacket().getValue();
		ClientHandler sender = (ClientHandler) netEvent.getSender();
		
		if(Ref.password.equals(password.getPassword())) {
			Server.nonRegisteredClientHandler.clear();
			for(ClientHandler ch: Server.getClientHandlers())	{
				if(sender.equals(ch)) {
					ch.setName(password.getName());
					try {
						ch.sendDatapacket(new Datapacket(DatapacketType.START_CLIENT_INTERFACE, DatapacketVoid.getDummy()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}	else Server.removeClient(sender);
	}
	
	
	
}
