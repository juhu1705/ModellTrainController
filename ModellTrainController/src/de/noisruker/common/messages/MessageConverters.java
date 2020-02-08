package de.noisruker.common.messages;

import static de.noisruker.server.loconet.messages.MessageType.OPC_INPUT_REP;
import static de.noisruker.server.loconet.messages.MessageType.OPC_LOCO_ADR;
import static de.noisruker.server.loconet.messages.MessageType.OPC_LOCO_DIRF;
import static de.noisruker.server.loconet.messages.MessageType.OPC_LOCO_SPD;
import static de.noisruker.server.loconet.messages.MessageType.OPC_SL_RD_DATA;
import static de.noisruker.server.loconet.messages.MessageType.OPC_SW_REQ;

import de.noisruker.server.loconet.messages.LocoNetMessage;
import de.noisruker.server.loconet.messages.MessageConverter;

public class MessageConverters {

	@MessageConverter(messageType = OPC_LOCO_SPD)
	public static SpeedMessage locoNetMessageToSpeedMessage(LocoNetMessage message) {
//		Ref.LOGGER.config("Start Converting");
		byte[] values = message.getValues();
		if (values == null || values.length < 2)
			return null;

		return new SpeedMessage(values[0], values[1]);
	}

	@MessageConverter(messageType = OPC_LOCO_DIRF)
	public static DirectionMessage locoNetMessageToDirectionMessage(LocoNetMessage message) {
//		Ref.LOGGER.config("Start Converting");
		byte[] values = message.getValues();
		if (values == null || values.length < 2)
			return null;

		return new DirectionMessage(values[0], values[1]);
	}

	@MessageConverter(messageType = OPC_LOCO_ADR)
	public static RequestSlotMessage locoNetMessageToRequestSlotMessage(LocoNetMessage message) {
//		Ref.LOGGER.config("Start Converting");
		byte[] values = message.getValues();
		if (values == null || values.length < 2)
			return null;

		return new RequestSlotMessage(values[1]);
	}

	@MessageConverter(messageType = OPC_SW_REQ)
	public static SwitchMessage locoNetMessageToSwitchMessage(LocoNetMessage message) {
//		Ref.LOGGER.config("Start Converting");

		byte[] values = message.getValues();
		if (values == null || values.length < 2)
			return null;

		boolean state;

//		Ref.LOGGER.info(values[1] + "");

		if (values[1] == (byte) 16)
			state = true;
		else if (values[1] == (byte) 48)
			state = false;
		else
			return null;

		return new SwitchMessage(values[0], state);
	}

	@MessageConverter(messageType = OPC_INPUT_REP)
	public static SensorMessage locoNetMessageToSensorMessage(LocoNetMessage message) {
//		Ref.LOGGER.config("Start Converting");
		byte[] values = message.getValues();
		if (values == null || values.length < 2)
			return null;

		return new SensorMessage(values[0], values[1]);
	}

	@MessageConverter(messageType = OPC_SL_RD_DATA)
	public static TrainSlotMessage locoNetMessageToTrainSlotMessage(LocoNetMessage message) {
//		Ref.LOGGER.config("Start Converting");
		byte[] values = new byte[message.getValues().length - 1];

		for (int i = 0; i < values.length; i++)
			values[i] = message.getValues()[i + 1];

		return new TrainSlotMessage(values);
	}

}
