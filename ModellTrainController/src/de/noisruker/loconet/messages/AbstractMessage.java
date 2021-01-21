package de.noisruker.loconet.messages;

import java.io.IOException;
import java.io.Serializable;

public interface AbstractMessage extends Serializable {

	LocoNetMessage toLocoNetMessage();

	void send();

}
