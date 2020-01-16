package de.noisruker.common.messages;

import java.io.Serializable;

public class ChatMessage implements Serializable {

	private String message, senderName, senderLevel;

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
