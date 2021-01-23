package de.noisruker.loconet.messages;

import java.io.Serializable;

public class RequestSlotMessage implements Serializable, AbstractMessage {

    byte address;

    public RequestSlotMessage(byte address) {
        this.address = address;
    }

    @Override
    public LocoNetMessage toLocoNetMessage() {
        return new LocoNetMessage(MessageType.OPC_LOCO_ADR, (byte) 0, address);
    }

    @Override
    public void send() {
        this.toLocoNetMessage().send();
    }

}
