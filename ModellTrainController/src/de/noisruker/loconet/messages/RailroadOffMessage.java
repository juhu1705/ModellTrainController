package de.noisruker.loconet.messages;

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
