package de.noisruker.main;

import de.noisruker.net.datapackets.NetEventDistributor;
import de.noisruker.server.Events;

public class Main  {

	public static void main(String[] args) throws InterruptedException {
		NetEventDistributor ned = new NetEventDistributor();
		ned.registerEventHandlers(Events.class);
		
		
		
	}
	
	

}
