package de.ancash.ilibrary.sockets;

import java.io.Serializable;

class Packet implements Serializable{

	private static final long serialVersionUID = 4980431645378802722L;
	private final String owner;
	private final TargetType target;
	
	public Packet(String owner, TargetType target) {
		this.owner = owner;
		this.target = target;
	}

	public TargetType getTarget() {
		return target;
	}
	
	public String getOwner() {
		return owner;
	}
}
