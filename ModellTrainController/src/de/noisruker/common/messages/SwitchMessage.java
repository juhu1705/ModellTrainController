package de.noisruker.common.messages;

import static de.noisruker.server.loconet.messages.MessageType.OPC_SW_REQ;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;

import de.noisruker.client.Client;
import de.noisruker.net.Side;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketType;
import de.noisruker.server.loconet.LocoNetConnection.PortNotOpenException;
import de.noisruker.server.loconet.messages.LocoNetMessage;
import de.noisruker.util.Ref;
import jssc.SerialPortException;

public class SwitchMessage implements AbstractMessage, Serializable {

	private byte address;
	private boolean on;

	public SwitchMessage(byte address, boolean state) {
		this.address = address;

		this.on = state;

	}

	@Override
	public void send() throws IOException {
		if (Ref.side.equals(Side.SERVER))
			try {
				this.toLocoNetMessage().send();
			} catch (SerialPortException | PortNotOpenException e) {
				Ref.LOGGER.log(Level.SEVERE, "Server not opened", e);
			}
		else
			Client.getConnectionHandler().sendDatapacket(new Datapacket(DatapacketType.SEND_SWITCH_MESSAGE, this));
	}

	@Override
	public LocoNetMessage toLocoNetMessage() {
		if (on)
			return new LocoNetMessage(OPC_SW_REQ, address, (byte) 16);
		else
			return new LocoNetMessage(OPC_SW_REQ, address, (byte) 48);
	}

}
