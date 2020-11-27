package de.noisruker.loconet;

import java.util.LinkedList;

import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import jssc.SerialPort;
import jssc.SerialPortException;

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
		this.connectionPort.setParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_NONE);

		this.isOpen = true;

		new Thread(() -> {
			byte[] bytes = new byte[0];
			while (this.isOpen) {
				byte[] actual = null;
				try {
					actual = this.connectionPort.readBytes(1);
				} catch (SerialPortException e) {
					Ref.LOGGER.config("Cannot read byte");
				}
				if (actual == null)
					continue;
				byte nextByte = actual[0];

				if (Util.getCheckSum(bytes) == nextByte) {
					byte[] newBytes = new byte[bytes.length + 1];

					System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
					newBytes[newBytes.length - 1] = nextByte;

					bytes = newBytes;

					this.receivedMessages.add(bytes.clone());

					bytes = new byte[0];
				} else {
					byte[] newBytes = new byte[bytes.length + 1];

					System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
					newBytes[newBytes.length - 1] = nextByte;

					bytes = newBytes;
				}

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
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
