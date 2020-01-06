package de.noisruker.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import de.noisruker.net.EventManager;
import de.noisruker.net.Side;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketSender;
import de.noisruker.net.datapackets.NetEvent;
import de.noisruker.net.datapackets.NetEventDistributor;
import de.noisruker.util.Ref;

/**
 * Behandelt die clientseitige Verbindung zum Server
 *
 * @author NominoX
 */

public class ConnectionHandler implements Runnable, DatapacketSender {

	private Socket serverSocket;
	private ObjectInputStream objectIn;
	private ObjectOutputStream objectOut;
	private Thread thread;
	private String hostIp;
	private NetEventDistributor ned;
	private ArrayList<Datapacket> events = new ArrayList<Datapacket>();

	ConnectionHandler(Socket serverSocket, String hostIp, NetEventDistributor ned) throws IOException {
		this.ned = ned;
		this.hostIp = hostIp;
		this.serverSocket = serverSocket;
		this.objectOut = new ObjectOutputStream(serverSocket.getOutputStream());
		this.objectOut.flush();
		this.objectIn = new ObjectInputStream(serverSocket.getInputStream());
		this.thread = new Thread(this);
		this.thread.start();
	}

//	public void reconnect() {
//		try {
//			Thread.sleep(1000);
//			serverSocket = new Socket(hostIp, Ref.STANDARD_HOST_PORT);
//			Client.setConnectionHandler(new ConnectionHandler(serverSocket, hostIp, this.ned));
//			this.thread.interrupt();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public void run() {
		Thread t = new Thread(new EventManager(this));
		t.start();
		while (true) {
			Datapacket dp = null;
			try {
				dp = Datapacket.receive(this.objectIn);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			assert dp != null;
			if (dp.getType().getSenderSide() != Side.SERVER) {
				System.err.println("[CLIENT] Warnung: Nicht-Serverseitiges Datenpaket empfangen, wird ignoriert!");
				return;
			}

			Ref.LOGGER.finest("HAha" + dp.getType().toString());
			// System.out.println("Added Datapacket");
			this.ned.addEventToQueue(new NetEvent(this, dp));
		}
	}

	public Datapacket getNextEvent() {
		return events.isEmpty() ? null : events.remove(0);
	}

	/**
	 * Sendet ein Datenpaket zum Server
	 *
	 * @param dp zu sendendes Datenpaket
	 * @throws IOException falls das Senden fehlschlägt
	 */
	public void sendDatapacket(Datapacket dp) throws IOException {
		dp.send(this.objectOut);
	}
}
