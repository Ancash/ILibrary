package de.ancash.ilibrary.sockets;

public class Request extends Packet{

	private static final long serialVersionUID = 3921896627870860330L;

	private final String request;
	
	public Request(String owner, String request, TargetType target) {
		super(owner, target);
		this.request = request;
	}
	
	public String getRequest() {
		return request;
	}
}
