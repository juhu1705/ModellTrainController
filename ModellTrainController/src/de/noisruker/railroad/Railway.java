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

    private HashMap<Integer, AbstractRailroadElement> way = new HashMap<>();

    private HashMap<Switch, Integer> waitForSwitch = new HashMap<>();

    private int actualIndex = 0, startIndex = 0, positionIndex = 0;

    private final boolean priorWayTrue;

    public Railway(Position from, Position to, Position lastPosition, boolean priorWayTrue) {
        this.from = from;
        this.to = to;
        this.lastPos = lastPosition;
        this.actual = lastPosition;
        this.priorWayTrue = priorWayTrue;
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
        while(!(actual instanceof Switch) || lastPos == null || !((Switch) actual).isSwitchPossible(lastPos)) {
            if(actual == null)
                break;
            lastPos = this.lastPos;
            actual = removeLast();
            if(actual != null)
                Ref.LOGGER.info("Actual: " + actual + "; Index: " + this.actualIndex + "; Position: " + actual.getPosition() + "; Last Position: " + this.lastPos + "; Possible: " + ((actual instanceof Switch) ? ((Switch) actual).isSwitchPossible(this.lastPos) : "Nop"));
        }
        if(actual == null)
            return;
        Switch aSwitch = (Switch) actual;
        Ref.LOGGER.info("Test " + ((Switch) actual).isSwitchPossible(this.lastPos));
        appendElement(aSwitch);
    }

    public AbstractRailroadElement removeLast() {
        if(actualIndex <= 1)
            return null;

        actualIndex--;
        AbstractRailroadElement toRemove = way.remove(actualIndex);

        this.lastPos = this.getPreviousElement() != null ? this.getPreviousElement().getPosition() : this.getLastElement().getToPos(toRemove.getPosition());
        this.actual = this.getLastElement().getPosition();

        return toRemove;
    }

    private AbstractRailroadElement getPreviousElement() {
        if(way.containsKey(actualIndex - 2))
            return way.get(actualIndex - 2);
        return null;
    }

    private AbstractRailroadElement goAhead() {
        Position newPos = this.getLastElement().getToPos(this.lastPos);
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

    boolean fail = false;

    public Railway calculateRailway() {
        do {
            if(this.goAhead() == null)
                return null;
        } while (!(this.getLastElement() instanceof Sensor));
        this.setStartIndex();
        this.checkLastElement();
        while (!this.to.equals(this.actual) && !fail) {
            if(this.goAhead() == null) {
                this.goToLastSwitch();
            }
            this.checkLastElement();
        }
        this.handleDuplicatedSwitches();



        return this;
    }

    private void handleDuplicatedSwitches() {
        HashMap<Switch, Integer> foundSwitches = new HashMap<>();
        for(int i = 0; i < this.actualIndex; i++) {
            AbstractRailroadElement e = this.way.get(i);
            if(i >= this.startIndex) {
                if (e instanceof Switch) {
                    if (((Switch) e).isSwitchPossible(this.way.get(i - 1).getPosition())) {
                        if(foundSwitches.containsKey(e)) {
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
        for(int i = start; i < to; i++) {
            this.way.remove(i);
        }
        for(int i = to; i < this.actualIndex; i++) {
            this.way.put(start + i - to, this.way.get(i));
        }
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
                    Ref.LOGGER.info("I am here " + this.removeLast() + "; Index: " + this.actualIndex);

                    this.goToLastSwitch();
                    Ref.LOGGER.info("I am here 1 " + this.actualIndex + "; " + this.getLastElement());

                    AbstractRailroadElement e = this.removeLast();

                    if(e == null) {
                        fail = true;
                        return;
                    } else {
                        Ref.LOGGER.info("Check");
                        this.appendElement(e);
                    }
                    this.checkLastElement();
                    return;
                }

                if(!this.isEditable())
                    return;

                usedSwitches.get(s).wayFalse = true;
                Position newPos = s.getNextPositionSwitchSpecial(this.lastPos, !this.priorWayTrue);
                if(newPos == null || newPos.equals(this.lastPos))
                    return;
                this.appendElement(LocoNet.getRailroad().getElementByPosition(newPos));

            } else {
                this.usedSwitches.put(s, new UsedWayIndicator());
                Position newPos = s.getNextPositionSwitchSpecial(this.lastPos, this.priorWayTrue);
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
            if(train.nextNextSensor.getTrain() != null && train.nextNextSensor.equals(train.nextNextSensor.getTrain().previousSensor)) {
                train.stopAdd = train.nextNextSensor;
            } else
                train.stopTrain();
        } else {
            train.nextNextSensor.addTrain(train);
            this.activateSwitches();
        }
    }

    public boolean isShorterThan(Railway railway) {
        if(railway == null)
            return true;
        return this.actualIndex < railway.actualIndex;
    }

    private class UsedWayIndicator {
        boolean wayTrue = true;
        boolean wayFalse = false;
    }
}
