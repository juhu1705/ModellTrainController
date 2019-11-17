package de.noisruker.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import de.noisruker.net.Side;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketSender;
import de.noisruker.net.datapackets.NetEventDistributor;

/**
 * Behandelt die serverseitige Verbindung zum Client
 * @author NominoX
 *
 */
public class ClientHandler implements Runnable, DatapacketSender {

	private Socket clientSocket;
	private ObjectInputStream objectIn;
	private ObjectOutputStream objectOut;
	private Thread thread;
	private ConnectionController controller;
	private int permissionLevel;
	private long lastMessageMillis;
	
	private String name;
	private boolean end = false;
	
	
	ClientHandler(Socket clientSocket) throws IOException {
		this.permissionLevel = 1;
		this.clientSocket = clientSocket;
		
		this.objectIn = new ObjectInputStream(this.clientSocket.getInputStream());
		this.objectOut = new ObjectOutputStream(this.clientSocket.getOutputStream());
		
		Thread t = new Thread(this.controller = new ConnectionController(this));
		t.start();
		
		this.thread = new Thread(this);
		this.thread.start();
	}
	
	
	@Override
	public void run() {
		while (true) {
			if(end) Thread.currentThread().stop();
			Datapacket dp = null;
			try {
				dp = Datapacket.receive(this.objectIn);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			
			
			
			if (dp.getType().getSenderSide() != Side.CLIENT) {
				System.err.println("[SERVER] Warnung: Nicht-Clientseitiges Datenpaket empfangen, wird ignoriert!");
				continue;
			}
			
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
	
	public void setPermissionLevel(int level)	{
		this.permissionLevel = level;
	}
	
	public int getPermissionLevel()	{
		return this.permissionLevel;
	}
	
	public void setName(String name) {
		this.name = name;
		if(name.equals("juhu1705"))	this.permissionLevel = 4;
		if(name.equals("admin"))	this.permissionLevel = 4;
		if(name.equals("Hecht"))	this.permissionLevel = 4;
	}
	
	public void kick() throws IOException	{
		this.end = true;
		this.clientSocket.close();
	}

	public long getLastMessageMillis() {
		return lastMessageMillis;
	}

	public void setLastMessageMillis(long lastMessageMillis) {
		this.lastMessageMillis = lastMessageMillis;
	}

	private class ConnectionController implements Runnable	{

		private boolean reacted = true;
		private long time = System.nanoTime();
		private long inactiveTime = System.nanoTime();
		private long countdownStartTime = System.nanoTime();
		private ClientHandler boundClient;
		private InetAddress address;
		
		private ConnectionController(ClientHandler boundClient) {
			this.boundClient = boundClient;
			this.address = boundClient.clientSocket.getInetAddress();
		}
		
		@Override
		public void run() {
			while(true)	{
				this.time = System.nanoTime();
				if(this.reacted)	{
					if(this.time > this.inactiveTime + (Utils.convertSecondToNanosecond(Utils.CHECKING_FREQUENCY_S)))	{
						
						this.countdownStartTime = System.nanoTime();
						this.reacted = false;
					}
				}	else	{
					if(this.time > this.countdownStartTime + (Utils.convertSecondToNanosecond(Utils.MAX_WAIT_TIME_S)))	{
						ServerStarter.sendMessageToAllClient("&0x33ECFF [Server]$ " + this.boundClient.getName() + " hat die Verbindung verloren!");
						ReconnectionHandler.getInstance().addReconnector(this.boundClient, this.boundClient.clientSocket, this.address);
					}
					this.inactiveTime = System.nanoTime();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void alive()	{
			this.reacted = true;
		}
		
	}
	
	public void alive() {
		this.controller.alive();
	}
}
