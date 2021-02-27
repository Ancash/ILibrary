package de.ancash.ilibrary.sockets;

public class InfoPacket extends Packet{

	private static final long serialVersionUID = -8004558991958337217L;

	private final String msg;
	
	public InfoPacket(String owner,String msg, TargetType target) {
		super(owner, target);
		this.msg = msg;
	}
	
	public String getMsg() {
		return msg;
	}
}
