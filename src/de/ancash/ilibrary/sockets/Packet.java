package de.ancash.ilibrary.sockets;

import java.io.Serializable;

public class Packet implements Serializable{

	private static final long serialVersionUID = 4980431645378802722L;
	private final String owner;
	private final Object o;
	
	public <T extends Serializable> Packet(String owner, T ser) {
		this.owner = owner;
		this.o = ser;
	}

	public Object getObject() {
		return o;
	}
	
	public String getOwner() {
		return owner;
	}
}
