package de.noisruker.net.datapackets;

/**
 * Enthält Metadaten über ein empfangenes Datenpaket
 * 
 * @author Niklas
 */
public class NetEvent {

	private final DatapacketSender sender;
	private final Datapacket datapacket;


	public NetEvent(DatapacketSender sender, Datapacket datapacket) {
		this.sender = sender;
		this.datapacket = datapacket;
	}


	public DatapacketSender getSender() {
		return this.sender;
	}


	public Datapacket getDatapacket() {
		return this.datapacket;
	}

}
