package de.noisruker.loconet.messages;

import static de.noisruker.loconet.messages.MessageType.*;

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

    @MessageConverter(messageType = OPC_GPON)
    public static RailroadOnMessage locoNetMessageToRailroadOnMessage(LocoNetMessage message) {
        return new RailroadOnMessage();
    }

    @MessageConverter(messageType = OPC_GPOFF)
    public static RailroadOffMessage locoNetMessageToRailroadOffMessage(LocoNetMessage message) {
        return new RailroadOffMessage();
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
            state = false;
        else if (values[1] == (byte) 48)
            state = true;
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
