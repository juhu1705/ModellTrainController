package de.noisruker.railroad;

import de.noisruker.loconet.LocoNet;
import de.noisruker.railroad.elements.AbstractRailroadElement;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.railroad.elements.Switch;
import de.noisruker.util.Ref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Railway {

    private Position from, to, actual, lastPos;

    private HashMap<Switch, UsedWayIndicator> usedSwitches = new HashMap<>();

    private HashMap<Integer, AbstractRailroadElement> way = new HashMap<>();

    private HashMap<Switch, Integer> waitForSwitch = new HashMap<>();

    private int actualIndex = 0, startIndex = 0, positionIndex = 0;

    public Railway(Position from, Position to, Position lastPosition) {
        this.from = from;
        this.to = to;
        this.lastPos = lastPosition;
        this.actual = lastPosition;
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

    public Switch goToLastSwitch() {
        AbstractRailroadElement actual = removeLast();
        while(!(actual instanceof Switch) || getPreviousElement() != null && !((Switch) actual).isSwitchPossible(getPreviousElement().getPosition())) {
            if(getPreviousElement() == null)
                break;
            if(actual == null)
                break;
            actual = removeLast();
        }
        if(!(actual instanceof Switch))
            return null;
        Switch aSwitch = (Switch) actual;
        appendElement(aSwitch);
        return aSwitch;
    }

    public AbstractRailroadElement removeLast() {
        if(actualIndex <= 1)
            return null;

        actualIndex--;
        AbstractRailroadElement toRemove = way.remove(actualIndex);

        this.lastPos = this.getPreviousElement() != null ? this.getPreviousElement().getPosition() : this.getLastElement().getToPos(this.actual);
        this.actual = this.getLastElement().getPosition();

        return toRemove;
    }

    private AbstractRailroadElement getPreviousElement() {
        if(way.containsKey(actualIndex - 2))
            return way.get(actualIndex - 2);
        return null;
    }

    private AbstractRailroadElement goAhead() {
        Ref.LOGGER.info("Go from " + this.getLastElement());
        Position newPos = this.getLastElement().getToPos(this.lastPos);
        Ref.LOGGER.info("To " + newPos);
        if(newPos == null || newPos.equals(this.lastPos))
            return null;
        this.appendElement(LocoNet.getRailroad().getElementByPosition(newPos));

        return this.getLastElement();
    }

    public Sensor getNextSensor() {
        AbstractRailroadElement element = next();
        while (!(element instanceof Sensor)) {
            if(element == null)
                break;

            if(element instanceof Switch) {
                Switch s = (Switch) element;
                this.waitForSwitch.put(s, this.positionIndex);
            }

            element = next();
        }
        return (Sensor) element;
    }

    public void activateSwitches() {
        Ref.LOGGER.info("Length: " + this.waitForSwitch.size());
        for(Map.Entry<Switch, Integer> r: this.waitForSwitch.entrySet()) {
            Ref.LOGGER.info("Switch " + r.getKey().setSwitchTo(this.way.get(r.getValue() - 1).getPosition(),
                    this.way.get(r.getValue() + 1).getPosition()));
        }
        this.waitForSwitch.clear();
    }

    private AbstractRailroadElement next() {
        positionIndex++;
        if(positionIndex < this.actualIndex) {
            return this.way.get(positionIndex);
        }
        positionIndex--;
        return null;
    }

    private AbstractRailroadElement prev() {
        positionIndex--;
        if(positionIndex >= 0) {
            return this.way.get(positionIndex);
        }
        positionIndex++;
        return null;
    }

    public Railway calculateRailway() {
        do {
            if(this.goAhead() == null)
                return null;
        } while (!(this.getLastElement() instanceof Sensor));
        this.setStartIndex();
        this.checkLastElement();
        while (!this.to.equals(this.actual)) {
            if(this.goAhead() != null)
                this.checkLastElement();
            else
                this.goToLastSwitch();
        }
        return this;
    }

    private void setStartIndex() {
        this.startIndex = this.actualIndex;
    }

    private boolean isEditable() {
        return this.actualIndex >= this.startIndex;
    }

    private void checkLastElement() {
        AbstractRailroadElement element = this.getLastElement();
        if(element instanceof Switch)
            this.handleSwitch((Switch) element);
    }

    private void handleSwitch(Switch s) {
        if(s.isSwitchPossible(this.lastPos)) {
            if(usedSwitches.containsKey(s)) {
                if(usedSwitches.get(s).wayFalse) {
                    this.removeLast();
                    this.goToLastSwitch();
                    this.checkLastElement();
                    return;
                }

                if(!this.isEditable())
                    return;

                usedSwitches.get(s).wayFalse = true;
                Position newPos = s.getNextPositionSwitchSpecial(this.lastPos, false);
                if(newPos == null || newPos.equals(this.lastPos))
                    return;
                this.appendElement(LocoNet.getRailroad().getElementByPosition(newPos));

            } else {
                this.usedSwitches.put(s, new UsedWayIndicator());
                Position newPos = s.getNextPositionSwitchSpecial(this.lastPos, true);
                if(newPos == null || newPos.equals(this.lastPos))
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
        train.nextSensor = this.getNextSensor();
        if(train.nextSensor == null) {
            train.destination = null;
            return;
        }
        if(!train.nextSensor.isFree(train)) {
            train.stopTrain();
        } else {
            train.nextSensor.addTrain(train);
            this.activateSwitches();
        }

        if(train.nextSensor.equals(train.destination))
            return;

        train.nextNextSensor = this.getNextSensor();

        if(train.nextNextSensor == null) {
            train.destination = null;
            return;
        }
        if(!train.nextNextSensor.isFree(train)) {
            train.stopTrain();
        } else {
            train.nextNextSensor.addTrain(train);
            this.activateSwitches();
        }
    }

    private class UsedWayIndicator {
        boolean wayTrue = true;
        boolean wayFalse = false;
    }
}
