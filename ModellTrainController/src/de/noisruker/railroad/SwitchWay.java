package de.noisruker.railroad;

import java.util.HashMap;

public class SwitchWay {

	private final Track a;
	private final Track b;

	private final HashMap<Switch, Boolean> states = new HashMap<>();

	public SwitchWay(Track a, Track b) {
		this.a = a;
		this.b = b;
	}

	public SwitchWay addSwitch(Switch s, boolean state) {
		this.states.put(s, Boolean.valueOf(state));
		return this;
	}

	public void use() {
		this.states.forEach((Switch s, Boolean b) -> s.setAndUpdateState(b.booleanValue()));
	}

	public boolean connect(Track a, Track b) {
		return ((this.a.equals(a) && this.b.equals(b)) || (this.a.equals(b) && this.b.equals(a)));
	}

}
