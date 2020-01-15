package de.noisruker.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import de.noisruker.net.Side;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketSender;
import de.noisruker.net.datapackets.NetEvent;
import de.noisruker.net.datapackets.NetEventDistributor;
import de.noisruker.util.Ref;

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
	private PermissionLevel permissionLevel;
	private long lastMessageMillis;

	private String name;
	private boolean end = false;

	ClientHandler(Socket clientSocket) throws IOException {
		this.permissionLevel = PermissionLevel.SPECTATOR;
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
			if (end)
				Thread.currentThread().interrupt();
			Datapacket dp = null;
			try {
				dp = Datapacket.receive(this.objectIn);
			} catch (ClassNotFoundException | IOException e) {
				Ref.LOGGER.info("Client disconnect: " + this.name);
			}

			if (dp == null)
				return;

			if (dp.getType().getSenderSide() != Side.CLIENT) {
				System.err.println("[SERVER] Warnung: Nicht-Clientseitiges Datenpaket empfangen, wird ignoriert!");
				continue;
			}

			if (this.permissionLevel.isEqualOrGreate(dp.getType().getNeededPermissionLevel()))
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
	}

	public PermissionLevel getPermissionLevel() {
		return this.permissionLevel;
	}

	public void setPermissionLevel(PermissionLevel level) {
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

	public enum PermissionLevel {
		SPECTATOR(0), MEMBER(1), SUPPORTER(2), CONTROLLER(3), ADMIN(4);

		int level;

		PermissionLevel(int level) {
			this.level = level;
		}

		public int getLevel() {
			return this.level;
		}

		public boolean isGreater(PermissionLevel plevel) {
			return this.level > plevel.level;
		}

		public boolean isEqualOrGreate(PermissionLevel plevel) {
			return this.level >= plevel.level;
		}

		public static PermissionLevel getByLevel(int level) {
			for (PermissionLevel pl : PermissionLevel.values())
				if (pl.level == level)
					return pl;
			return SPECTATOR;
		}
	}
}
