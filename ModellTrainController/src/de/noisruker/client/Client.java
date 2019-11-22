package de.noisruker.client;

import de.noisruker.net.datapackets.NetEventDistributor;
import de.noisruker.util.Ref;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public final class Client {

	private static ConnectionHandler connectionHandler;

	private Client() {

	}


	/**
	 * Verbindet zum Server
	 *
	 * @param hostIp IP des Servers
	 * @throws UnknownHostException Wenn der Host nicht gefunden wurde
	 * @throws IOException          Wenn beim Verbinden ein sonstiger Fehler auftritt
	 */
	public static void connectToServer(String hostIp) throws UnknownHostException, IOException {
		Socket serverSocket = new Socket(hostIp, Ref.STANDARD_HOST_PORT);
		NetEventDistributor ned = new NetEventDistributor();
		ned.registerEventHandlers(Events.class);
		ned.startProcessing();
		Client.connectionHandler = new ConnectionHandler(serverSocket, hostIp, ned);
	}

	public static ConnectionHandler getConnectionHandler() {
		return Client.connectionHandler;
	}

	public static void setConnectionHandler(ConnectionHandler handler) {
		Client.connectionHandler = handler;
	}
}
