package de.noisruker.net.datapackets;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import de.noisruker.util.Ref;

/**
 * Ermöglicht vereinfachte Behandlung von eintreffenden {@link Datapacket
 * Datenpaketen}. Verwaltet {@link NetEventHandler} und die EventQueue.
 *
 * @author Niklas
 */
public class NetEventDistributor {

	private final Map<Method, DatapacketType> eventHandlers = new HashMap<>();

	private final LinkedList<NetEvent> eventQueue = new LinkedList<>();

	private Thread eventRunner;
	private boolean processing = true;

	public NetEventDistributor() {
		this.eventRunner = new Thread(() -> {
			while (processing) {
				if (!eventQueue.isEmpty()) {
					NetEvent event = eventQueue.removeFirst();

					try {
						this.processEvent(event);
					} catch (InvocationTargetException e) {
						Ref.LOGGER.log(Level.SEVERE, "Exception in EventHandler:", e.getCause());
					} catch (IllegalAccessException | IllegalArgumentException e) {
						e.printStackTrace();
					}
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, "eventRunner");

		this.eventRunner.setDaemon(true);
	}

	/**
	 * Registriert alle mit {@link NetEventHandler} annotiierten Methoden der Klasse
	 * {@code eventListener}
	 *
	 * @param eventListener Klasse, deren {@link NetEventHandler}-Methoden
	 *                      registriert werden sollen
	 * @see #registerEventHandler(Method)
	 */
	public void registerEventHandlers(Class<?> eventListener) {
		for (Method m : eventListener.getMethods()) {
			if (m.getAnnotation(NetEventHandler.class) != null) {
				this.registerEventHandler(m);
			}
		}
	}

	/**
	 * Registriert eine Methode als EventHandler. <br>
	 * <br>
	 * <p>
	 * Diese muss folgende Bedingungen erfüllen:<br>
	 * <ul>
	 * <li>muss die Annotation {@link NetEventHandler} enthalten</li>
	 * <li>muss statisch sein</li>
	 * <li>muss exakt einen Parameter enthalten, über den das {@link NetEvent}
	 * übergeben wird</li>
	 * </ul>
	 *
	 * @param eventHandler EventHandler-Methode
	 * @throws IllegalArgumentException wenn eine der oben genannten Bedingungen
	 *                                  nicht erfüllt ist
	 * @see #registerEventHandlers(Class)
	 */
	public void registerEventHandler(Method eventHandler) {
		NetEventHandler annotation = eventHandler.getAnnotation(NetEventHandler.class);
		if (annotation == null) {
			throw new IllegalArgumentException("Angegebene Methode hat keine NetEventHandler-Annotation");
		}
		Class<?>[] paramTypes = eventHandler.getParameterTypes();
		DatapacketType type = annotation.type();

		if (!(paramTypes.length == 1 && paramTypes[0] == NetEvent.class)) {
			throw new IllegalArgumentException(
					"Angegebene Methode muss genau einen Parameter vom Typ NetEvent enthalten");
		}

		if (!Modifier.isStatic(eventHandler.getModifiers())) {
			throw new IllegalArgumentException("Angegebene Methode muss statisch sein");
		}

		this.eventHandlers.put(eventHandler, type);
	}

	/**
	 * Fügt ein {@link NetEvent} zur Warteschlange hinzu, sodass dieses vom
	 * eventRunner-Thread verarbeitet wird, sobald alle vorherigen {@link NetEvent
	 * NetEvents} verarbeitet wurden
	 *
	 * @param event das hinzuzufügende Event
	 */
	public void addEventToQueue(NetEvent event) {
		this.eventQueue.addLast(event);
	}

	/**
	 * Startet den EventRunner, sodass Events verarbeitet werden können
	 *
	 * @see #stopProcessing()
	 */
	public void startProcessing() {
		this.processing = true;
		// eventRunner starten
		if (!this.eventRunner.isAlive())
			this.eventRunner.start();
	}

	/**
	 * Stoppt den EventRunner
	 *
	 * @see #startProcessing()
	 */
	public void stopProcessing() {
		this.processing = false;
	}

	/**
	 * Ruft ein Datenpaket-Event auf
	 *
	 * @param event Datenpaket-Event
	 */
	private void processEvent(NetEvent event)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Datapacket dp = event.getDatapacket();

		for (Entry<Method, DatapacketType> e : this.eventHandlers.entrySet()) {
			if (e.getValue() == dp.getType()) {
				e.getKey().invoke(null, event);
			}
		}
	}

}
