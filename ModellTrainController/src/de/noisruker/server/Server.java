package de.noisruker.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import de.noisruker.common.messages.PasswordRequest;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketType;
import de.noisruker.net.datapackets.NetEventDistributor;
import de.noisruker.util.Ref;

/**
 * Serverklasse
 *
 * @author NominoX
 */
public final class Server implements Runnable {

	public static List<ClientHandler> nonRegisteredClientHandler = new ArrayList<>();
	private static List<ClientHandler> clientHandlers = new ArrayList<>();
	private static ServerSocket serverSocket;

	private Server() {

	}

	public static ServerSocket getSocket() {
		return serverSocket;
	}

	/**
	 * Wartet auf Verbindungen eines Clients und überschreibt ggf. den aktuellen
	 * ClientHandler
	 *
	 * @return Einen Client-Handler
	 * @throws IOException wenn ein I/O Fehler auftritt
	 */
	public static ClientHandler waitForClient() throws IOException {
		ClientHandler newClient = new ClientHandler(Server.serverSocket.accept());
		Server.clientHandlers.add(newClient);
		Server.nonRegisteredClientHandler.add(newClient);
		return newClient;
	}

	public static void start() throws IOException {
		Server.serverSocket = new ServerSocket(Ref.STANDARD_HOST_PORT);
		NetEventDistributor ned = new NetEventDistributor();
		ned.registerEventHandlers(Events.class);
		ned.startProcessing();

		new Thread(new Server()).start();
	}

	public static void removeClient(ClientHandler client) {
		Server.clientHandlers.remove(client);
		Server.nonRegisteredClientHandler.remove(client);

		try {
			client.kick();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addClientHandlers(ClientHandler ch) {
		clientHandlers.add(ch);
	}

	public static List<ClientHandler> getClientHandlers() {
		return new ArrayList<>(Server.clientHandlers);
	}

	public static void removeClientOfClientHandler(ClientHandler player) {
		Server.clientHandlers.remove(player);
	}

	@Override
	public void run() {
		while (true) {
			try {
				ClientHandler client = Server.waitForClient();

				Ref.LOGGER.info("Client try to join");

				client.sendDatapacket(new Datapacket(DatapacketType.PASSWORD_REQUEST, new PasswordRequest()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
