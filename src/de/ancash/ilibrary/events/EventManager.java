package de.ancash.ilibrary.events;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventManager {
    private static final Logger LOGGER = Logger.getLogger(EventManager.class.getCanonicalName());

    private EventManager() {
    	
    }

    public static <T extends IEvent> T callEvent(T event) {
        IHandlerList handlers = event.getHandlers();
        ListenerRegistration[] listeners = handlers.getRegisteredListeners();

        if (listeners != null) {
            for (ListenerRegistration listener : listeners) {
                try {
                    if (!event.isCancelled() || listener.getOrder().ignoresCancelled()) {
                        listener.getExecutor().execute(event);
                    }
                } catch (Throwable ex) {
                    LOGGER.log(Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + listener.getOwner().getClass().getName(), ex);
                }
            }
        }
        return event;
    }

    public static void registerEvents(IListener iListener, Object owner) {
        for (Map.Entry<Class<? extends IEvent>, Set<ListenerRegistration>> entry : createRegisteredListeners(iListener, owner).entrySet()) {
            Class<? extends IEvent> delegatedClass = getRegistrationClass(entry.getKey());
            if (!entry.getKey().equals(delegatedClass)) {
                LOGGER.severe("Plugin attempted to register delegated event class " + entry.getKey() + ". It should be using " + delegatedClass + "!");
                continue;
            }
            getEventListeners(delegatedClass).registerAll(entry.getValue());
        }
    }
    
    public static void registerEvent(Class<? extends IEvent> iEvent, Order priority, EventExecutor executor, Object owner) {
        getEventListeners(iEvent).register(new ListenerRegistration(executor, priority, owner));
    }

    /**
     * Returns the specified event type's HandlerList
     *
     * @param type EventType to lookup
     * @return HandlerList The list of registered handlers for the event.
     */
    private static IHandlerList getEventListeners(Class<? extends IEvent> type) {
        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (IHandlerList) method.invoke(null);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    private static Class<? extends IEvent> getRegistrationClass(Class<? extends IEvent> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(IEvent.class) && IEvent.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(IEvent.class));
            } else {
                throw new IllegalArgumentException("Unable to find handler list for event " + clazz.getName());
            }
        }
    }

    public static Map<Class<? extends IEvent>, Set<ListenerRegistration>> createRegisteredListeners(final IListener iListener, Object plugin) {
        Map<Class<? extends IEvent>, Set<ListenerRegistration>> ret = new HashMap<Class<? extends IEvent>, Set<ListenerRegistration>>();
        Method[] methods;
        try {
            methods = iListener.getClass().getDeclaredMethods();
        } catch (NoClassDefFoundError e) {
            LOGGER.severe("Plugin " + plugin.getClass().getSimpleName() + " is attempting to register event " + e.getMessage() + ", which does not exist. Ignoring events registered in " + iListener.getClass());
            return ret;
        }
        for (final Method method : methods) {
            final IEventHandler eh = method.getAnnotation(IEventHandler.class);
            if (eh == null) {
                continue;
            }
            final Class<?> checkClass = method.getParameterTypes()[0];
            Class<? extends IEvent> eventClass;
            if (!IEvent.class.isAssignableFrom(checkClass) || method.getParameterTypes().length != 1) {
                LOGGER.severe("Wrong method arguments used for event type registered");
                continue;
            } else {
                eventClass = checkClass.asSubclass(IEvent.class);
            }
            method.setAccessible(true);
            Set<ListenerRegistration> eventSet = ret.get(eventClass);
            if (eventSet == null) {
                eventSet = new HashSet<ListenerRegistration>();
                ret.put(eventClass, eventSet);
            }
            eventSet.add(new ListenerRegistration(new EventExecutor() {

                public void execute(IEvent iEvent) throws EventException {
                    try {
                        if (!checkClass.isAssignableFrom(iEvent.getClass())) {
                            throw new EventException("Wrong event type passed to registered method");
                        }
                        method.invoke(iListener, iEvent);
                    } catch (Throwable t) {
                        throw new EventException(t);
                    }
                }

            }, eh.order(), plugin));
        }
        return ret;
    }
}
