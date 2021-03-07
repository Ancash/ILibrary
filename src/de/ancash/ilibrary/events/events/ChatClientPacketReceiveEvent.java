package de.ancash.ilibrary.events.events;

import de.ancash.ilibrary.events.IEvent;
import de.ancash.ilibrary.events.IHandlerList;
import de.ancash.ilibrary.sockets.Packet;

public final class ChatClientPacketReceiveEvent extends IEvent{
	
	private static final IHandlerList handlers = new IHandlerList();
	
	public static IHandlerList getHandlerList() {
        return handlers;
    }
	
	@Override
	public IHandlerList getHandlers() {
		return handlers;
	}

	private final Packet packet;
	
	public ChatClientPacketReceiveEvent(Packet packet) {
		this.packet = packet;
	}
	
	public Packet getPacket() {
		return packet;
	}
}
