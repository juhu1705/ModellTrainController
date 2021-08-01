package de.noisruker.loconet.messages;

public enum MessageType {

    // INFO: Befehle die nur aus dem OPC0DE bestehen:
    /**
     * Lässt alle Züge anhalten.
     */
    OPC_IDLE((byte) 0x85, (byte) 1),
    /**
     * Schaltet die Modellbahn ein.
     */
    OPC_GPON((byte) 0x83, (byte) 1),
    /**
     * Schaltet die Modellbahn aus
     */
    OPC_GPOFF((byte) 0x82, (byte) 1),
    /**
     *
     */
    OPC_BUSY((byte) 0x81, (byte) 1),

    // INFO: Befehle die aus dem OPCODE und weiteren eingaben bestehen.
    /**
     * Weißt der mitgegebenen Addresse einen neuen Slot zu.
     */
    OPC_LOCO_ADR((byte) 0xBF, (byte) 3),
    /**
     * Setzt die Richtung eines Zuges. Hierbei muss die Nummer des SLOTS bekannt
     * sein.
     */
    OPC_LOCO_DIRF((byte) 0xA1, (byte) 3),
    /**
     * Setzt die Geschwindigkeit des Zuges. Hierbei muss die Nummer des SLOTS
     * bekannt sein.
     */
    OPC_LOCO_SPD((byte) 0xA0, (byte) 3),
    /**
     * Verändert den Status eines Magnetartikels.
     */
    OPC_SW_REQ((byte) 0xB0, (byte) 3),
    /**
     * Sensor Rückmeldung
     */
    OPC_INPUT_REP((byte) 0xB2, (byte) 3), OPC_SL_RD_DATA((byte) 0xE7, (byte) 12);

    byte opCode, length;

    MessageType(byte activator, byte length) {
        this.opCode = activator;
        this.length = length;
    }

    public byte getOpCode() {
        return this.opCode;
    }

    public byte getLength() {
        return length;
    }

}
