package de.noisruker.railroad;

import de.noisruker.loconet.LocoNet;
import de.noisruker.railroad.elements.AbstractRailroadElement;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.railroad.elements.Switch;
import de.noisruker.util.Ref;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Railway {

    private Position from, to, actual, lastPos;

    private HashMap<Switch, UsedWayIndicator> usedSwitches = new HashMap<>();

    protected HashMap<Integer, AbstractRailroadElement> way = new HashMap<>();

    protected HashMap<Switch, Integer> waitForSwitch = new HashMap<>();

    protected int actualIndex = 0, startIndex = 0, positionIndex = 0, positionIndex1 = 0;

    private final boolean priorWayTrue, checkSensors;

    public Railway(Position from, Position to, Position lastPosition, boolean priorWayTrue, boolean checkSensors) {
        this.from = from;
        this.to = to;
        this.lastPos = lastPosition;
        this.actual = lastPosition;
        this.priorWayTrue = priorWayTrue;
        this.checkSensors = checkSensors;
        appendElement(LocoNet.getRailroad().getElementByPosition(from));
    }

    public AbstractRailroadElement getLastElement() {
        return way.get(actualIndex - 1);
    }

    public void appendElement(AbstractRailroadElement e) {
        this.lastPos = this.actual;
        this.actual = e.getPosition();
        way.put(actualIndex, e);
        actualIndex++;
    }

    public void goToLastSwitch() {
        Position lastPos = this.lastPos;
        AbstractRailroadElement actual = removeLast();
        while (!(actual instanceof Switch) || lastPos == null || !((Switch) actual).isSwitchPossible(lastPos)) {
            if (actual == null)
                break;
            lastPos = this.lastPos;
            actual = removeLast();
        }
        if (actual == null)
            return;
        if (actualIndex < this.startIndex) {
            this.fail = true;
            return;
        }
        Switch aSwitch = (Switch) actual;
        appendElement(aSwitch);
    }

    public AbstractRailroadElement removeLast() {
        if (actualIndex <= 1)
            return null;

        actualIndex--;
        AbstractRailroadElement toRemove = way.remove(actualIndex);

        this.lastPos = this.getPreviousElement() != null ? this.getPreviousElement().getPosition() : this.getLastElement().getToPos(toRemove.getPosition());
        this.actual = this.getLastElement().getPosition();

        return toRemove;
    }

    private AbstractRailroadElement getPreviousElement() {
        if (way.containsKey(actualIndex - 2))
            return way.get(actualIndex - 2);
        return null;
    }

    private AbstractRailroadElement goAhead() {
        Position newPos = this.getLastElement().getToPos(this.lastPos);
        if (newPos == null || newPos.equals(this.lastPos))
            return null;
        this.appendElement(LocoNet.getRailroad().getElementByPosition(newPos));

        return this.getLastElement();
    }

    public void setLastPosToTrain(Train train, Sensor sensor) {
        AbstractRailroadElement element = this.nextForLastPositionSetting();
        while (!(element instanceof Sensor) && !sensor.equals(element)) {
            if (element == null)
                break;

            element = nextForLastPositionSetting();
        }
        if (element != null && this.positionIndex1 - 1 >= 0)
            train.updateLastPosition(this.way.get(this.positionIndex1 - 1).getPosition());
    }

    public Sensor getNextSensor(Sensor sensor) {
        AbstractRailroadElement element = next();
        while (!(element instanceof Sensor)) {
            if (element == null)
                break;

            if (element instanceof Switch) {
                Switch s = (Switch) element;
                this.waitForSwitch.put(s, this.positionIndex);
            }

            element = next();
        }
        if (element != null && sensor.getAddress() == ((Sensor) element).getAddress())
            return this.getNextSensor(sensor);
        return (Sensor) element;
    }

    public void activateSwitches(HashMap<Switch, Integer> switches) {
        for (Map.Entry<Switch, Integer> r : switches.entrySet()) {
            Ref.LOGGER.info("Switch " + r.getKey().setSwitchTo(this.way.get(r.getValue() - 1).getPosition(),
                    this.way.get(r.getValue() + 1).getPosition()));
        }
    }

    public HashMap<Switch, Integer> getSwitches() {
        HashMap<Switch, Integer> switches = (HashMap<Switch, Integer>) this.waitForSwitch.clone();
        this.waitForSwitch.clear();
        return switches;
    }

    private AbstractRailroadElement next() {
        positionIndex++;
        if (positionIndex < this.actualIndex) {
            return this.way.get(positionIndex);
        }
        positionIndex--;
        return null;
    }

    private AbstractRailroadElement nextForLastPositionSetting() {
        positionIndex1++;
        if (positionIndex1 < this.actualIndex) {
            return this.way.get(positionIndex1);
        }
        positionIndex1--;
        return null;
    }

    private AbstractRailroadElement prev() {
        positionIndex--;
        if (positionIndex >= 0) {
            return this.way.get(positionIndex);
        }
        positionIndex++;
        return null;
    }

    boolean fail = false;

    public Railway calculateRailway() {
        do {
            if (this.goAhead() == null)
                return null;
        } while (!(this.getLastElement() instanceof Sensor));
        this.setStartIndex();
        this.checkLastElement();
        while (!this.to.equals(this.actual) && !fail) {
            if (this.goAhead() == null) {
                this.goToLastSwitch();
            }
            this.checkLastElement();
        }
        if (fail)
            return null;

        this.handleDuplicatedSwitches();

        return this;
    }

    private void handleDuplicatedSwitches() {
        HashMap<Switch, Integer> foundSwitches = new HashMap<>();
        for (int i = 0; i < this.actualIndex; i++) {
            AbstractRailroadElement e = this.way.get(i);
            if (i >= this.startIndex) {
                if (e instanceof Switch) {
                    if (((Switch) e).isSwitchPossible(this.way.get(i - 1).getPosition())) {
                        if (foundSwitches.containsKey(e)) {
                            int firstIndex = foundSwitches.get(e);
                            this.delete(firstIndex, i);
                        } else
                            foundSwitches.put((Switch) e, i);
                    }
                }
            }
        }


    }

    private void delete(final int start, final int to) {
        for (int i = start; i < to; i++) {
            this.way.remove(i);
        }
        for (int i = to; i < this.actualIndex; i++) {
            this.way.put(start + i - to, this.way.remove(i));
        }

        actualIndex -= (to - start);
    }

    private void setStartIndex() {
        this.startIndex = this.actualIndex;
    }

    private boolean isEditable() {
        return this.actualIndex >= this.startIndex;
    }

    private HashMap<Sensor, Integer> sensorCheckHashMap = new HashMap<>();

    private void checkLastElement() {
        AbstractRailroadElement element = this.getLastElement();
        if (element instanceof Switch)
            this.handleSwitch((Switch) element);
        else if (checkSensors && element instanceof Sensor) {
            if (((Sensor) element).getState()) {
                if (this.sensorCheckHashMap.containsKey(element) && this.sensorCheckHashMap.get(element) == this.actualIndex)
                    this.fail = true;
                this.sensorCheckHashMap.put((Sensor) element, this.actualIndex);
                this.goToLastSwitch();
                if (this.fail)
                    return;
                this.checkLastElement();
            }
        }
    }

    private void handleSwitch(Switch s) {
        if (s.isSwitchPossible(this.lastPos)) {
            if (usedSwitches.containsKey(s)) {
                if (usedSwitches.get(s).wayFalse) {
                    this.removeLast();
                    this.goToLastSwitch();

                    AbstractRailroadElement e = this.removeLast();

                    if (e == null) {
                        fail = true;
                        return;
                    } else {
                        this.appendElement(e);
                    }
                    this.checkLastElement();
                    return;
                }

                if (!this.isEditable())
                    return;

                usedSwitches.get(s).wayFalse = true;
                Position newPos = s.getNextPositionSwitchSpecial(this.lastPos, !this.priorWayTrue);
                if (newPos == null || newPos.equals(this.lastPos))
                    return;
                this.appendElement(LocoNet.getRailroad().getElementByPosition(newPos));

            } else {
                this.usedSwitches.put(s, new UsedWayIndicator());
                Position newPos = s.getNextPositionSwitchSpecial(this.lastPos, this.priorWayTrue);
                if (newPos == null || newPos.equals(this.lastPos))
                    return;
                this.appendElement(LocoNet.getRailroad().getElementByPosition(newPos));
            }
            this.checkLastElement();
        }
    }

    public void setPositions(Train train) {
        train.prev = Objects.requireNonNull(this.getPreviousElement()).getPosition();
    }

    public void init(Train train) {
        train.nextSensor = this.getNextSensor(train.actualSensor);
        if (train.nextSensor == null) {
            train.destination = null;
            return;
        }
        if (!train.nextSensor.isFree(train)) {
            train.stopTrain();
            train.waitForSwitch.put(train.actualSensor, this.getSwitches());
            return;
        } else {
            if(!train.equals(train.nextSensor.getTrain()))
                train.nextSensor.addTrain(train);
            this.activateSwitches(this.getSwitches());
        }

        if (train.nextSensor.equals(train.destination))
            return;

        train.nextNextSensor = this.getNextSensor(train.nextSensor);

        if (train.nextNextSensor == null) {
            train.destination = null;
            return;
        }
        if (!train.nextNextSensor.isFree(train)) {
            if (train.nextNextSensor.getTrain() != null &&
                    train.nextNextSensor.equals(train.nextNextSensor.getTrain().previousSensor)) {
                train.stopAdd = train.nextNextSensor;
            } else
                train.stopTrain();
        } else {
            if(train.actualSensor.equals(train.nextNextSensor) || !train.equals(train.nextNextSensor.getTrain()))
                train.nextNextSensor.addTrain(train);
        }
        train.waitForSwitch.put(train.nextSensor, this.getSwitches());
    }

    public boolean isShorterThan(Railway railway) {
        if (railway == null)
            return true;
        return this.actualIndex < railway.actualIndex;
    }

    @Override
    public String toString() {
        return "Railway{" +
                "from=" + from +
                ", to=" + to +
                ", actualIndex=" + actualIndex +
                ", fail=" + fail +
                '}';
    }

    public void print() {
        Ref.LOGGER.info(this::toString);
        way.forEach((k, e) -> Ref.LOGGER.info(k + ": " + e));
    }

    private class UsedWayIndicator {
        boolean wayTrue = true;
        boolean wayFalse = false;
    }
}
