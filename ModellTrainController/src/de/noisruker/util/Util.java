package de.noisruker.util;

import de.noisruker.server.loconet.messages.MessageType;

public class Util {

	public static byte getCheckSum(byte... bytes) {
		byte xoredbydes = 0;

		for (byte b : bytes)
			xoredbydes ^= b;

		return (byte) ~xoredbydes;
	}

	public static byte[] addCheckSum(byte... bytes) {
		byte[] b = new byte[bytes.length + 1];
		for (int i = 0; i < bytes.length; i++)
			b[i] = bytes[i];

		b[b.length - 1] = Util.getCheckSum(bytes);
		return b;
	}

	public static byte[] addOpCode(byte opCode, byte... bytes) {
		byte[] b = new byte[bytes.length + 1];
		b[0] = opCode;

		for (int i = 0; i < bytes.length; i++)
			b[i + 1] = bytes[i];

		return b;
	}

	public static MessageType getMessageType(byte opCode) {
		for (MessageType m : MessageType.values())
			if (m.getOpCode() == opCode)
				return m;
		return null;
	}

}
