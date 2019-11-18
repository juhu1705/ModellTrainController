package de.noisruker.net.datapackets;

import de.noisruker.net.Side;
import java.io.Serializable;
import de.noisruker.client.*;
import de.noisruker.server.*;


/**
 * Typen von Datenpaketen
 *
 * @author Niklas
 */
public enum DatapacketType {

	HAS_FUNKTION(Side.SERVER, Boolean.class),
	PASSWORD_REQUEST(Side.SERVER, PasswordRequest.class),
	PASSWORD_ANSWER(Side.CLIENT, ClientPassword.class),
	START_CLIENT_INTERFACE(Side.SERVER, DatapacketVoid.class),
	SEND_COMMAND(Side.CLIENT, String.class);

	private final Class<? extends Serializable> requiredValueType;
	private final Side senderSide;


	DatapacketType(Side senderSide, Class<? extends Serializable> requiredValueType) {
		this.senderSide = senderSide;
		this.requiredValueType = requiredValueType;
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
