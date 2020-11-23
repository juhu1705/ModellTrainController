package de.noisruker.loconet;

import de.noisruker.loconet.messages.AbstractMessage;

public interface LocoNetMessageListener {

	public void progressMessage(AbstractMessage message);

}
