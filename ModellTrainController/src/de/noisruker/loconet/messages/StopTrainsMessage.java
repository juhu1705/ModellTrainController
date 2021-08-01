package de.noisruker.loconet.messages;

public class StopTrainsMessage implements AbstractMessage {
    @Override
    public LocoNetMessage toLocoNetMessage() {
        return new LocoNetMessage(MessageType.OPC_IDLE);
    }

    @Override
    public void send() {
        this.toLocoNetMessage().send();
    }
}
