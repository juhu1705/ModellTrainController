package de.noisruker.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.util.Ref;

public class ClientConnector {

	public Socket clientSocket;
	
	public ObjectOutputStream objOut;
	
	public ObjectInputStream objIn;
	
	public ServerConnector(String host) throws UnknownHostException, IOException {
		clientSocket = new Socket(host, Ref.STANDARD_HOST_PORT);
		
		objOut = new ObjectOutputStream(clientSocket.getOutputStream());
		
		objOut.flush();
		
		objIn = new ObjectInputStream(clientSocket.getInputStream());
	}
	
	public void sendDatapacket(Datapacket dp) throws IOException {
		dp.send(objOut);
	}
	
}
