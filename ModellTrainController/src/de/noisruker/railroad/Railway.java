package de.noisruker.railroad;

import de.noisruker.loconet.LocoNet;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.railroad.elements.Switch;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.Notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Railway {

    private Position from, to, actual, lastPos;

    private HashMap<Switch, UsedWayIndicator> usedSwitches = new HashMap<>();

    private HashMap<Integer, AbstractRailroadElement> way = new HashMap<>();

    private int actualIndex = 0, startIndex = 0;

    public Railway(Position from, Position to, Position lastPosition) {
        this.from = from;
        this.to = to;
        this.lastPos = lastPosition;
        this.actual = this.from;
        appendElement(LocoNet.getRailroad().getElementByPosition(this.actual));
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
        while(!(actual instanceof Switch)) {
            actual = removeLast();
        }
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
        Position newPos = this.getLastElement().getToPos(this.lastPos);
        if(newPos == null || newPos == this.getLastElement().getPosition())
            return null;
        this.appendElement(LocoNet.getRailroad().getElementByPosition(newPos));
        return this.getLastElement();
    }

    public void findSensor(Sensor s) {

    }

    public void findSwitch(Switch s) {

    }

    public Railway calculateRailway() {
        do {
            this.goAhead();
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
                Switch aSwitch = s;
                if(usedSwitches.get(s).wayFalse) {
                    this.removeLast();
                    aSwitch = this.goToLastSwitch();
                }

                if(!this.isEditable()) {
                    while(!s.equals(this.getLastElement()))
                        goAhead();
                    aSwitch = (Switch) this.getLastElement();
                }

                if(usedSwitches.containsKey(aSwitch)) {
                    UsedWayIndicator indicator = usedSwitches.get(aSwitch);
                    if(indicator != null) {
                        if(indicator.wayFalse && aSwitch.equals(s))
                            return;
                        else if(indicator.wayFalse) {
                            while(!s.equals(this.getLastElement()))
                                goAhead();
                            aSwitch = (Switch) this.getLastElement();

                            if(usedSwitches.get(aSwitch).wayFalse)
                                return;

                            aSwitch.getNextPositionSwitchSpecial(this.lastPos, false);
                            usedSwitches.get(aSwitch).wayFalse = true;
                            this.checkLastElement();
                        } else {
                            aSwitch.getNextPositionSwitchSpecial(this.lastPos, false);
                            indicator.wayFalse = true;
                            this.checkLastElement();
                        }
                    }
                }
            } else {
                this.usedSwitches.put(s, new UsedWayIndicator());
                s.getNextPositionSwitchSpecial(this.lastPos, true);
            }
        }
    }

    private class UsedWayIndicator {
        boolean wayTrue = true;
        boolean wayFalse = false;
    }
}
