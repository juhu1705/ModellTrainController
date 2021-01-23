package de.noisruker.loconet.messages;

import java.io.Serializable;

import static de.noisruker.loconet.messages.MessageType.OPC_LOCO_SPD;

public class SpeedMessage implements AbstractMessage, Serializable {

    private final byte speed;
    private final byte slot;

    public SpeedMessage(byte slot, byte speed) {
        this.speed = speed;
        this.slot = slot;
    }

    public byte getSpeed() {
        return this.speed;
    }

    public byte getSlot() {
        return this.slot;
    }

    @Override
    public LocoNetMessage toLocoNetMessage() {
        return new LocoNetMessage(OPC_LOCO_SPD, slot, speed);
    }

    @Override
    public void send() {
        this.toLocoNetMessage().send();
    }

    @Override
    public String toString() {
        return "[SpeedMessage for Train " + slot + " to speed " + speed + "]";
    }

}
