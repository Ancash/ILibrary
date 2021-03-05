package de.ancash.ilibrary.events;

public interface EventExecutor {
    public void execute(IEvent iEvent) throws EventException;
}
