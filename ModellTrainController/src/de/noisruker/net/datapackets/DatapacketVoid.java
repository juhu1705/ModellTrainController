package de.noisruker.net.datapackets;

import de.noisruker.util.Ref;

import java.io.Serializable;


/**
 * Dummy-Klasse zum Versenden als Typ in einem {@link Datapacket}
 *
 * @author Niklas
 */
public final class DatapacketVoid implements Serializable {

	private static final long serialVersionUID = Ref.UNIVERSAL_SERIAL_VERSION_UID;

	private static final DatapacketVoid vObj = new DatapacketVoid();


	private DatapacketVoid() {
	}


	/**
	 * @return das universelle DatapacketVoid-Objekt
	 */
	public static DatapacketVoid getDummy() {
		return DatapacketVoid.vObj;
	}
}
