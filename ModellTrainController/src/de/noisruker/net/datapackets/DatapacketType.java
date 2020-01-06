package de.noisruker.net.datapackets;

import static de.noisruker.server.ClientHandler.PermissionLevel.ADMIN;
import static de.noisruker.server.ClientHandler.PermissionLevel.MEMBER;
import static de.noisruker.server.ClientHandler.PermissionLevel.SPECTATOR;

import java.io.Serializable;

import de.noisruker.client.ClientPassword;
import de.noisruker.common.ChatMessage;
import de.noisruker.common.messages.AbstractMessage;
import de.noisruker.common.messages.SpeedMessage;
import de.noisruker.common.messages.SwitchMessage;
import de.noisruker.net.Side;
import de.noisruker.server.ClientHandler.PermissionLevel;
import de.noisruker.server.PasswordRequest;

/**
 * Typen von Datenpaketen
 *
 * @author Niklas
 */
public enum DatapacketType {

	CLIENT_SEND_CHAT_MESSAGE(Side.CLIENT, ChatMessage.class, MEMBER),
	SERVER_SEND_CHAT_MESSAGE(Side.SERVER, ChatMessage.class, MEMBER),
	PASSWORD_REQUEST(Side.SERVER, PasswordRequest.class, SPECTATOR),
	PASSWORD_ANSWER(Side.CLIENT, ClientPassword.class, SPECTATOR),
	START_CLIENT_INTERFACE(Side.SERVER, DatapacketVoid.class, SPECTATOR),
	SEND_SWITCH_MESSAGE(Side.CLIENT, SwitchMessage.class, MEMBER),
	SEND_SPEED_MESSAGE(Side.CLIENT, SpeedMessage.class, MEMBER),
	SERVER_SEND_MESSAGE(Side.SERVER, AbstractMessage.class, MEMBER), SEND_COMMAND(Side.CLIENT, String.class, ADMIN);

	private final Class<? extends Serializable> requiredValueType;
	private final Side senderSide;
	private final PermissionLevel minLevel;

	DatapacketType(Side senderSide, Class<? extends Serializable> requiredValueType, PermissionLevel minLevel) {
		this.senderSide = senderSide;
		this.requiredValueType = requiredValueType;
		this.minLevel = minLevel;
	}

	/**
	 * @return Die minimal benötigten Berechtigungen, um diese Nachricht dem Server
	 *         zulassen kommen zu können.
	 */
	public PermissionLevel getNeededPermissionLevel() {
		return this.minLevel;
	}

	/**
	 * @return Den vom jeweiligen Datenpakettypen erwarteten Wert-Typen
	 */
	public Class<? extends Serializable> getRequiredValueType() {
		return this.requiredValueType;
	}

	/**
	 * @return Seite, von der das Paket gesendet wird
	 */
	public Side getSenderSide() {
		return this.senderSide;
	}
}
