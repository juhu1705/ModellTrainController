package de.noisruker.loconet;

import de.noisruker.loconet.messages.MessageType;
import de.noisruker.util.Config;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.util.LinkedList;

public class LocoNetConnection {

    /**
     * Der serielle Port, über den die Modellbahn angesprochen wird.
     */
    private final SerialPort connectionPort;
    private boolean isOpen = false;

    private final LinkedList<byte[]> receivedMessages = new LinkedList<>();

    public LocoNetConnection(String port) {
        this.connectionPort = new SerialPort(port);
    }

    protected void send(byte[] message) throws PortNotOpenException, SerialPortException {
        if (!this.isOpen)
            throw new PortNotOpenException();

        this.connectionPort.writeBytes(message);
    }

    protected byte[] removeNextMessage() throws PortNotOpenException {
        if (!this.isOpen)
            throw new PortNotOpenException();

        return this.receivedMessages.poll();
    }

    public void open() throws SerialPortException {
        if (this.isOpen)
            return;

        this.connectionPort.openPort();
        this.connectionPort.setParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_NONE);

        this.isOpen = true;

        new Thread(() -> {
            MessageType lastType = null;
            byte[] bytes = new byte[0];
            while (this.isOpen) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }

                byte[] actual = null;
                try {
                    actual = this.connectionPort.readBytes(1);
                } catch (SerialPortException e) {
                    Ref.LOGGER.config("Cannot read byte");
                }
                if (actual == null) {
                    continue;
                }

                byte nextByte = actual[0];
                if (Util.getMessageType(nextByte) != null) {
                    if (lastType == null) {
                        lastType = Util.getMessageType(nextByte);
                    }
                    if (bytes.length > 3) {
                        if (MessageType.OPC_INPUT_REP.equals(lastType) && Config.reportAddress > 0) {
                            //new SwitchMessage((byte) (Config.reportAddress - 1), true).toLocoNetMessage().send();
                        }
                        lastType = Util.getMessageType(nextByte);
                        bytes = new byte[]{
                                nextByte
                        };
                        continue;
                    }
                }

                if (Util.getCheckSum(bytes) == nextByte) {
                    byte[] newBytes = new byte[bytes.length + 1];

                    System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
                    newBytes[newBytes.length - 1] = nextByte;

                    bytes = newBytes;

                    this.receivedMessages.add(bytes.clone());

                    bytes = new byte[0];
                    lastType = null;
                } else {
                    byte[] newBytes = new byte[bytes.length + 1];

                    System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
                    newBytes[newBytes.length - 1] = nextByte;

                    bytes = newBytes;
                }
            }
            Ref.LOGGER.info("Connection stopped");
        }).start();
    }

    public void close() throws SerialPortException {
        if (!this.isOpen)
            return;
        this.connectionPort.closePort();
        this.isOpen = false;
    }

    public static class PortNotOpenException extends Throwable {

    }

}
