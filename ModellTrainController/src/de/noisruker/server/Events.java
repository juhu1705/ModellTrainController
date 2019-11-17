package de.noisruker.server;

import de.noisruker.net.datapackets.DatapacketType;
import de.noisruker.net.datapackets.NetEvent;
import de.noisruker.net.datapackets.NetEventHandler;
import jssc.SerialPortException;

public class Events {

	@NetEventHandler(type = DatapacketType.SEND_COMMAND)
	public static void recieveCommand(NetEvent netEvent) {
		try {
			ModellRailroad.getInstance().sendCommand((String)netEvent.getDatapacket().getValue(), netEvent.getSender());
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}
	
}
