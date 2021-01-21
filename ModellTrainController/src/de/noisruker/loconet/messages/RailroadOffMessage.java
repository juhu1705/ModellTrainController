package de.noisruker.loconet.messages;

import java.io.IOException;

public class RailroadOffMessage implements AbstractMessage {
    @Override
    public LocoNetMessage toLocoNetMessage() {
        return new LocoNetMessage(MessageType.OPC_GPOFF);
    }

    @Override
    public void send() {
        this.toLocoNetMessage().send();
    }
}
