package de.noisruker.server.loconet;

import de.noisruker.common.messages.AbstractMessage;

public interface LocoNetMessageListener {

	public void progressMessage(AbstractMessage message);

}
