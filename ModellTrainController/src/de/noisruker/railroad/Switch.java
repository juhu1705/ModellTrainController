package de.noisruker.railroad;

import de.noisruker.loconet.messages.SwitchMessage;
import de.noisruker.loconet.LocoNetConnection.PortNotOpenException;
import jssc.SerialPortException;

public class Switch {

	private boolean state;
	private final byte address;

	public Switch(byte address) {
		this.address = address;
		this.setAndUpdateState(true);
	}

	public SwitchMessage getMessage() {
		return new SwitchMessage(address, state);
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public void setAndUpdateState(boolean state) {
		this.setState(state);
		try {
			this.getMessage().toLocoNetMessage().send();
		} catch (SerialPortException | PortNotOpenException e) {
			e.printStackTrace();
		}
	}

}