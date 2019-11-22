package de.noisruker.net;

import de.noisruker.client.ConnectionHandler;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.NetEvent;
import de.noisruker.net.datapackets.NetEventDistributor;

public class EventManager implements Runnable {

	private ConnectionHandler c;
	private NetEventDistributor eventDistributor;

	public EventManager(ConnectionHandler c) {
		this.eventDistributor = new NetEventDistributor();
		this.c = c;
	}

	@Override
	public void run() {
		while (true) {
			Datapacket dp = this.c.getNextEvent();
			if (dp != null) eventDistributor.addEventToQueue(new NetEvent(this.c, dp));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
