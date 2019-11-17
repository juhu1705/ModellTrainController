package de.noisruker.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.util.Ref;

public class ClientHandler implements Runnable {

	public ServerSocket serverSocket;
	
	public ObjectOutputStream objOut;
	
	public ObjectInputStream objIn;
	
	public ClientHandler() throws UnknownHostException, IOException {
		serverSocket = new ServerSocket(Ref.STANDARD_HOST_PORT);
	}
	
	
	
	public void sendDatapacket(Datapacket dp) throws IOException {
		dp.send(objOut);
	}



	@Override
	public void run() {
		while(true) {
			
		}
	}
	
}
