package de.noisruker.loconet;

import de.noisruker.loconet.messages.AbstractMessage;

public interface LocoNetMessageListener {

    void progressMessage(AbstractMessage message);

}
