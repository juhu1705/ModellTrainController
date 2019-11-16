package de.noisruker.net.datapackets;

import de.noisruker.net.Side;

import java.io.Serializable;


/**
 * Typen von Datenpaketen
 *
 * @author Niklas
 */
public enum DatapacketType {

	TEST(Side.SERVER, String.class);

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
