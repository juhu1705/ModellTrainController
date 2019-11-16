package de.noisruker.net.datapackets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import de.noisruker.util.Ref;



/**
 * Repräsentiert ein Datenpaket zum Austausch von Informationen zwischen Client
 * und Server
 * 
 * @author Niklas
 */
public class Datapacket implements Serializable {

	private static final long serialVersionUID = Ref.UNIVERSAL_SERIAL_VERSION_UID;

	private final DatapacketType type;
	private final Serializable value;


	public Datapacket(DatapacketType type, Serializable value) {
		Class<? extends Serializable> requiredValueType = type.getRequiredValueType();

		if (!value.getClass().isAssignableFrom(requiredValueType)) {
			throw new IllegalArgumentException(
					type.name() + " akzeptiert als Wert nur Instanzen von " + requiredValueType.getName());
		}

		this.type = type;
		this.value = value;
	}


	public DatapacketType getType() {
		return this.type;
	}


	public Serializable getValue() {
		return this.value;
	}


	public void send(ObjectOutputStream outStream) throws IOException {
		outStream.writeObject(this);
	}


	public static Datapacket receive(ObjectInputStream inStream) throws ClassNotFoundException, IOException {
		return (Datapacket) inStream.readObject();
	}
}
