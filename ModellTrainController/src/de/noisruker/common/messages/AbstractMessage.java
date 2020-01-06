package de.noisruker.common.messages;

import java.io.IOException;
import java.io.Serializable;

import de.noisruker.server.loconet.messages.LocoNetMessage;

public interface AbstractMessage extends Serializable {

	public LocoNetMessage toLocoNetMessage();

	public void send() throws IOException;

}
