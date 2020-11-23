package de.noisruker.loconet.messages;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.noisruker.loconet.LocoNetConnection.PortNotOpenException;
import de.noisruker.loconet.LocoNetMessageSender;
import jssc.SerialPortException;

public class LocoNetMessage {

	private MessageType messageType;

	private byte[] values;

	public LocoNetMessage(MessageType type, byte... values) {
		this.messageType = type;
		this.values = values;
	}

	public LocoNetMessage(AbstractMessage message) {
		LocoNetMessage copyMessage = message.toLocoNetMessage();

		this.values = copyMessage.values;
		this.messageType = copyMessage.messageType;
	}

	public byte[] getValues() {
		return this.values;
	}

	public MessageType getType() {
		return this.messageType;
	}

	public AbstractMessage toMessage()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		if (this.messageType == null)
			return null;

//		Ref.LOGGER
//				.config("Try to Convert " + this.messageType.toString() + "; " + LocoNetMessage.converters.toString());

		for (Method c : LocoNetMessage.converters)
			if (c.getAnnotation(MessageConverter.class).messageType().equals(this.messageType))
				return (AbstractMessage) c.invoke(this, this);

		return null;

	}

	public void send() throws SerialPortException, PortNotOpenException {
		LocoNetMessageSender.getInstance().sendMessage(this.messageType.opCode, values);
	}

	private static ArrayList<Method> converters = new ArrayList<>();

	public static boolean registerMessageConverter(Method converter) {
		if (converter == null || converter.getAnnotation(MessageConverter.class) == null)
			return false;

		// Ref.LOGGER.info(converter.toString());

		for (int i = 0; i < LocoNetMessage.converters.size();)
			if (LocoNetMessage.converters.get(i).getAnnotation(MessageConverter.class).messageType()
					.equals(converter.getAnnotation(MessageConverter.class).messageType()))
				LocoNetMessage.converters.remove(i);
			else
				i++;

		LocoNetMessage.converters.add(converter);

		return true;
	}

	public static void registerAll(Method... converters) {
		for (Method converter : converters)
			if (converter.isAnnotationPresent(MessageConverter.class))
				LocoNetMessage.registerMessageConverter(converter);
	}

	public static void registerClass(Class<Object> c) {
		LocoNetMessage.registerAll(c.getMethods());
	}

	public static void registerClasses(Class<Object>... cs) {
		for (Class<Object> c : cs)
			LocoNetMessage.registerAll(c.getMethods());
	}

	public static void registerStandart() {
		LocoNetMessage.registerAll(MessageConverters.class.getMethods());
	}

}
