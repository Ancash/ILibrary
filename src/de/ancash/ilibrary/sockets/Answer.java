package de.ancash.ilibrary.sockets;

import java.io.Serializable;

public class Answer extends Packet{

	private static final long serialVersionUID = 1069916462332476369L;

	private final Object answer;
	
	public <T extends Serializable> Answer(String owner, T answer, TargetType target) {
		super(owner, target);
		this.answer = answer;
	}
	
	public Object getAnswer() {
		return answer;
	}
}
