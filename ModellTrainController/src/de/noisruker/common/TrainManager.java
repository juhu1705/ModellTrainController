package de.noisruker.common;

import java.util.HashMap;

public class TrainManager {

	private HashMap<Byte, Train> trains = new HashMap<Byte, Train>();

	public TrainManager() {

	}

	public Train getTrain(byte address) {
		return this.trains.getOrDefault(address, new Train(address));
	}

}
