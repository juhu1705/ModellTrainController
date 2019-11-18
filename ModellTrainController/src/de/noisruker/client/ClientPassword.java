package de.noisruker.client;

import java.io.Serializable;

import de.noisruker.util.Ref;

public class ClientPassword implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = Ref.UNIVERSAL_SERIAL_VERSION_UID;
	private String password;
	private String name;
	
	public ClientPassword(String password, String name)	{
		this.password = password;
		this.name = name;
	}
	
	public String getPassword()	{
		return this.password;
	}
	
	public String getName()	{
		return name;
	}
}
