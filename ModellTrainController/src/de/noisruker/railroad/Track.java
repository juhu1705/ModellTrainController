package de.noisruker.railroad;

import java.util.ArrayList;
import java.util.LinkedList;

public class Track {

	private Sensor sensor;

	private String name;

	private Track actualNext = null, actualPrevious = null;

	private ArrayList<SwitchWay> switchWays = new ArrayList<>();

	private ArrayList<Switch> switches = new ArrayList<>();

	private Train train = null;

	private LinkedList<Train> trainsWaiting = new LinkedList<>();

	public Track(String name, Sensor sensor, Switch... switchs) {
		this.sensor = sensor;
		this.name = name;

		for (Switch s : switchs) {
			this.switches.add(s);
		}
	}

	public Track addSwitchWay(SwitchWay sw) {
		this.switchWays.add(sw);
		return this;
	}

	public Track addSwitchWays(SwitchWay... sws) {
		for (SwitchWay sw : sws)
			this.addSwitchWay(sw);
		return this;
	}

	public Sensor getSensor() {
		return this.sensor;
	}

	public Train getTrain() {
		return this.train;
	}

	public void onTrainDriveIn(Train t, Sensor s) {
		if (this.train != null) {
			t.stop();
			trainsWaiting.addLast(t);
		} else
			train = t;
	}

	public void onTrainDriveOut(Train t, Sensor s) {
		this.train = null;

	}

	public boolean isFree() {
		return this.train == null;
	}

	public Track getNext(Track actual, Track next, Track previous) {
		for (SwitchWay sw : this.switchWays) {
			if (sw.connect(next, previous)) {
				sw.use();
				return next;
			}
		}
		return null;
	}

	public void setTrain(Train train2) {
		this.train = train2;

	}

	@Override
	public String toString() {
		return this.name;
	}

}
