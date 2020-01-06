package de.noisruker.common;

import java.util.HashMap;

public class SwitchWay {

	private Track a, b;

	private HashMap<Switch, Boolean> states = new HashMap<>();

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
