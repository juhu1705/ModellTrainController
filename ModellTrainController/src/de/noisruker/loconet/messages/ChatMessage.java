package de.noisruker.loconet.messages;

import java.io.Serializable;

public class ChatMessage implements Serializable {

	private final String message;
    private String senderName;
    private String senderLevel;

	public ChatMessage(String message) {
		this.message = message;
		this.senderName = "";
		this.senderLevel = "SPECTATOR";
	}

	public ChatMessage setName(String name) {
		senderName = name;
		return this;
	}

	public ChatMessage setLevel(String level) {
		senderLevel = level;
		return this;
	}

	public String getFormatted() {
		return "[" + senderName + "|" + senderLevel + "] " + message + "\n";
	}

}
