package de.noisruker.common;

import java.util.HashMap;

public class TrainManager {
	
	private HashMap<Byte, Train> trains = new HashMap<Byte, Train>();
	
	public Train getTrain(byte address) {
		return this.trains.getOrDefault(Byte.valueOf(address), new Train(address));
	}
	
	public TrainManager() {
		
	}
	
}