package de.noisruker.loconet.messages;

public class RailroadOnMessage implements AbstractMessage {
    @Override
    public LocoNetMessage toLocoNetMessage() {
        return new LocoNetMessage(MessageType.OPC_GPON);
    }

    @Override
    public void send() {
        this.toLocoNetMessage().send();
    }
}
