package de.noisruker.server;

import de.noisruker.net.Side;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketSender;
import de.noisruker.net.datapackets.NetEvent;
import de.noisruker.net.datapackets.NetEventDistributor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Behandelt die serverseitige Verbindung zum Client
 *
 * @author NominoX
 */
public class ClientHandler implements Runnable, DatapacketSender {

	private Socket clientSocket;
	private ObjectInputStream objectIn;
	private ObjectOutputStream objectOut;
	private Thread thread;
	private NetEventDistributor eventDistributor;
	private int permissionLevel;
	private long lastMessageMillis;

	private String name;
	private boolean end = false;


	ClientHandler(Socket clientSocket) throws IOException {
		this.permissionLevel = 1;
		this.clientSocket = clientSocket;

		this.objectIn = new ObjectInputStream(this.clientSocket.getInputStream());
		this.objectOut = new ObjectOutputStream(this.clientSocket.getOutputStream());

		this.eventDistributor = new NetEventDistributor();
		this.eventDistributor.registerEventHandlers(Events.class);
		this.eventDistributor.startProcessing();

		this.thread = new Thread(this);
		this.thread.start();
	}


	@Override
	public void run() {
		while (true) {
			if (end) Thread.currentThread().interrupt();
			Datapacket dp = null;
			try {
				dp = Datapacket.receive(this.objectIn);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}

			if (dp == null)
				return;

			if (dp.getType().getSenderSide() != Side.CLIENT) {
				System.err.println("[SERVER] Warnung: Nicht-Clientseitiges Datenpaket empfangen, wird ignoriert!");
				continue;
			}

			this.eventDistributor.addEventToQueue(new NetEvent(this, dp));

		}
	}


	/**
	 * Sendet ein Datenpaket zum Client
	 *
	 * @param dp zu sendendes Datenpaket
	 * @throws IOException falls das Senden fehlschlägt
	 */
	public void sendDatapacket(Datapacket dp) throws IOException {
		dp.send(this.objectOut);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
		if (name.equals("juhu1705")) this.permissionLevel = 4;
		if (name.equals("admin")) this.permissionLevel = 4;
		if (name.equals("Hecht")) this.permissionLevel = 4;
	}

	public int getPermissionLevel() {
		return this.permissionLevel;
	}

	public void setPermissionLevel(int level) {
		this.permissionLevel = level;
	}

	public void kick() throws IOException {
		this.end = true;
		this.clientSocket.close();
	}

	public long getLastMessageMillis() {
		return lastMessageMillis;
	}

	public void setLastMessageMillis(long lastMessageMillis) {
		this.lastMessageMillis = lastMessageMillis;
	}
}
