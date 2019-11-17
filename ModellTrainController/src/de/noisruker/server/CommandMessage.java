package de.noisruker.server;

import de.noisruker.net.datapackets.DatapacketSender;

public class CommandMessage {

	private String command;
	private DatapacketSender sender;
	
	public CommandMessage(String command, DatapacketSender sender) {
		this.command = command;
		this.sender = sender;
	}
	
	public String getCommand() {
		return this.command;
	}
	
	public DatapacketSender getSender() {
		return this.sender;
	}
	
}
