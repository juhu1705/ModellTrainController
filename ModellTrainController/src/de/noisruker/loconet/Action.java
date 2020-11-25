package de.noisruker.loconet;

import java.io.IOException;
import java.util.ArrayList;

import de.noisruker.loconet.messages.AbstractMessage;
import de.noisruker.loconet.messages.SensorMessage;

public class Action {

	private final SensorMessage action;

	private final ArrayList<AbstractMessage> events = new ArrayList<>();

	public Action(SensorMessage action) {
		this.action = action;
	}

	public void addEvent(AbstractMessage e) {
		if (e != null)
			this.events.add(e);
	}

	public void fireEvents() throws IOException, InterruptedException {
		if (!events.isEmpty())
			for (AbstractMessage m : events) {
				if (m != null)
					m.send();
				Thread.sleep(100);
			}
	}

	public boolean isAction(SensorMessage action) {
		return this.action.getAddress() == action.getAddress() && this.action.getState() == action.getState();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Action)
			return this.isAction(((Action) obj).action);
		if (obj instanceof SensorMessage)
			return this.isAction((SensorMessage) obj);
		return false;
	}

	@Override
	public String toString() {
		return this.action.toString() + ": Actions: " + events.toString();
	}

	public boolean isEmpty() {
		return this.events.isEmpty();
	}

}
