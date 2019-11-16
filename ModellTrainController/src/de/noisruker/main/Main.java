package de.noisruker.main;

import java.nio.charset.StandardCharsets;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		String[] portNames = SerialPortList.getPortNames();
		for (int i = 0; i < portNames.length; i++) {
			System.out.println(portNames[i]);

		}

		SerialPort serialPort = new SerialPort("COM3");
		System.out.println(serialPort.getPortName());

		try {
			if (serialPort.openPort())
				System.out.println("open");// Open serial port
			serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_2,
					SerialPort.PARITY_NONE);// Set params. Also you can set params by this string:
											// serialPort.setParams(9600, 8, 1, 0);

			if (serialPort.writeBytes("v9v07".getBytes(StandardCharsets.UTF_8)))
				System.out.println("true");
			serialPort.addEventListener(l -> {
				try {
					System.out.println("Recieve");

					String s = new String(serialPort.readBytes(), StandardCharsets.UTF_8);

					System.out.println(s);
				} catch (SerialPortException e) {
					e.printStackTrace();
				}
			});

			// Close serial port
		} catch (SerialPortException ex) {
			System.out.println(ex);
		}
		while (true)
			Thread.sleep(1000);

	}

}
