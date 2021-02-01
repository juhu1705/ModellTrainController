package de.noisruker.railroad;

import de.noisruker.gui.GuiMain;
import de.noisruker.loconet.LocoNet;
import de.noisruker.loconet.LocoNetMessageReceiver;
import de.noisruker.loconet.messages.SensorMessage;
import de.noisruker.railroad.elements.AbstractRailroadElement;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.railroad.elements.Switch;
import de.noisruker.util.Ref;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.swing.*;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Railroad {

    private AbstractRailroadElement[][] railroadElements = null;

    public void applyRailroad(final AbstractRailroadElement[][] railroad) {
        if (railroad.length < 100)
            return;
        this.railroadElements = railroad;
        if (GuiMain.getInstance() != null)
            GuiMain.getInstance().checkOutRailroad();
    }

    public Railroad() {
    }

    public AbstractRailroadElement[][] getRailroad() {
        return railroadElements;
    }

    public AbstractRailroadElement getElementByPosition(Position position) {
        return this.railroadElements[position.getX()][position.getY()];
    }

    public void init() {
        LocoNetMessageReceiver.getInstance().registerListener(l -> {
            if (l instanceof SensorMessage) {
                final SensorMessage s = (SensorMessage) l;

                Ref.LOGGER.info("Sensor " + s.getAddress() + "; State: " + s.getState());

                this.handleMessage(s);
            }
        });
    }

    public Sensor getNextSensor(Sensor s, Train t) {
        Position lastPosition;
        if(s.equals(t.actualSensor))
            lastPosition = t.getPrevPosition();
        else if(t.railway != null) {
            AbstractRailroadElement lastElement = t.railway.way.get(t.railway.positionIndex);
            if (lastElement.equals(s))
                lastPosition = t.railway.way.get(t.railway.positionIndex - 1).getPosition();
            else {
                int index = t.railway.positionIndex;

                while (index > 0 && !t.railway.way.get(index).equals(s))
                    index--;

                if (index == 0) {
                    if (t.railway.way.get(index).equals(s)) {
                        lastPosition = t.railway.way.get(index).getToPos(t.railway.way.get(index + 1).getPosition());
                    } else {
                        while (index < t.railway.actualIndex && !s.equals(t.railway.way.get(index))) {
                            index++;
                        }

                        if (t.railway.way.containsKey(index) && t.railway.way.get(index).equals(s))
                            lastPosition = t.railway.way.get(index - 1).getPosition();
                        else
                            return null;
                    }
                } else
                    lastPosition = t.railway.way.get(index - 1).getPosition();
            }
        } else {
            Sensor actual = t.getActualPosition();
            AbstractRailroadElement element = this.getElementByPosition(actual.getToPos(t.prev));
            Position last = actual.getPosition();
            while(element.equals(s)) {
                if(element instanceof Switch) {
                    if(t.switchOnDestination.containsKey(element))
                        element = this.getElementByPosition(((Switch) element).getNextPositionSwitchSpecial(last,
                                t.switchOnDestination.get(element)));
                    else
                        element = this.getElementByPosition(element.getToPos(last));
                } else
                    element = this.getElementByPosition(element.getToPos(last));
            }
            lastPosition = last;
        }

        if(s.getToPos(lastPosition) == null)
            return null;

        Sensor next = goToNextSensor(this.getElementByPosition(s.getToPos(lastPosition)), s.getPosition(), t, s);

        return next;
    }

    private Sensor goToNextSensor(AbstractRailroadElement element, Position from, Train t, Sensor sensor) {
        if(element instanceof Sensor)
            return (Sensor) element;

        Position newPos = element.getToPos(from);

        if(element instanceof Switch) {
            if(!((Switch) element).isSwitchPossible(from))
                return null;

            Switch s = (Switch) element;

            int checkFrom = 0;
            boolean success = false;
            if(t.railway != null) {
                for (Map.Entry<Integer, AbstractRailroadElement> e : t.railway.way.entrySet()) {
                    if (e.getValue().equals(sensor)) {
                        checkFrom = e.getKey();
                    }
                }
                for (int i = checkFrom; i < t.railway.actualIndex; i++) {
                    if (t.railway.way.containsKey(i) && t.railway.way.containsKey(i - 1)) {
                        if (s.equals(t.railway.way.get(i)) && from.equals(t.railway.way.get(i - 1).getPosition())) {
                            if (t.railway.way.containsKey(i + 1)) {
                                newPos = t.railway.way.get(i + 1).getPosition();
                                success = true;
                            }
                        }
                    }
                }
            }

            if(!success) {
                newPos = s.getNextPositionSwitchSpecial(from, true);

                if(newPos == null || newPos.equals(from))
                    return null;
                AbstractRailroadElement newElement = this.getElementByPosition(newPos);
                if(newElement == null)
                    return null;

                Sensor sensor1 = goToNextSensor(newElement, element.getPosition(), t, sensor);
                if(sensor1 == null)
                    return null;
                if(sensor1.isFree(t)) {
                    t.switchOnDestination.put(s, true);
                    return sensor1;
                }

                newPos = s.getNextPositionSwitchSpecial(from, false);

                if(newPos == null || newPos.equals(from))
                    return null;
                AbstractRailroadElement newElement1 = this.getElementByPosition(newPos);
                if(newElement1 == null)
                    return null;

                Sensor sensor2 = goToNextSensor(newElement1, element.getPosition(), t, sensor);
                if(sensor2 == null)
                    return null;

                if(sensor2.isFree(t)) {
                    t.switchOnDestination.put(s, false);
                    return sensor2;
                }
            }
        }

        if(newPos == null || newPos.equals(from))
            return null;

        AbstractRailroadElement newElement = this.getElementByPosition(newPos);

        if(newElement == null)
            return null;

        return goToNextSensor(newElement, element.getPosition(), t, sensor);
    }

    private void handleMessage(SensorMessage s) {
        if (s.getState())
            trainEnter(s.getAddress());
        else
            trainLeft(s.getAddress());
    }

    public void initRailroad() throws IOException, SAXException {
        if (Files.exists(FileSystems.getDefault().getPath(Ref.HOME_FOLDER + "railroad.mtc"),
                LinkOption.NOFOLLOW_LINKS))
            openRailroad(Ref.HOME_FOLDER + "railroad.mtc");
    }

    public void openRailroad(String input) throws SAXException, IOException {
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();

        InputSource inputSource = new InputSource(new FileReader(input));

        xmlReader.setContentHandler(new RailroadReader());
        xmlReader.parse(inputSource);
    }

    public int trainsWithDestination() {
        int counter = 0;
        for (Train t : LocoNet.getInstance().getTrains()) {
            if (t.destination != null)
                counter++;
        }
        return counter;
    }

    public Railway findWay(Sensor from, Sensor to, Position lastPosition) {
        Railway priorTrueAndSensors = new Railway(from.getPosition(), to.getPosition(), lastPosition, true, true).calculateRailway();
        Railway priorFalseAndSensors = new Railway(from.getPosition(), to.getPosition(), lastPosition, false, true).calculateRailway();

        if (priorTrueAndSensors == null) {
            if (priorFalseAndSensors != null)
                return priorFalseAndSensors;
            Railway priorTrue = new Railway(from.getPosition(), to.getPosition(), lastPosition, true, false).calculateRailway();
            Railway priorFalse = new Railway(from.getPosition(), to.getPosition(), lastPosition, false, false).calculateRailway();
            if (priorTrue == null)
                return priorFalse;

            return priorTrue.isShorterThan(priorFalse) ? priorTrue : priorFalse;
        }
        return priorTrueAndSensors.isShorterThan(priorFalseAndSensors) ? priorTrueAndSensors : priorFalseAndSensors;
    }

    public void trainEnter(final int nodeAddress) {
        LocoNet.getInstance().getTrains().forEach(t -> t.trainEnter(nodeAddress));
        Sensor.getAllSensors().forEach(s -> s.onTrainEnter(nodeAddress));
    }

    public void trainLeft(final int nodeAddress) {
        LocoNet.getInstance().getTrains().forEach(t -> t.trainLeft(nodeAddress));
        Sensor.getAllSensors().forEach(s -> s.onTrainLeft(nodeAddress));
    }

    public void update() {
        LocoNet.getInstance().getTrains().forEach(Train::update);
        Sensor.getAllSensors().forEach(Sensor::update);
    }

    private boolean stopTrainControlSystem = true;

    public void startTrainControlSystem() {
        if (stopTrainControlSystem)
            new Thread(this::trainControl).start();
    }

    private boolean isStopped = true;

    public boolean isControlSystemStopped() {
        return isStopped;
    }

    public void stopTrainControlSystem() {
        stopTrainControlSystem = true;
    }

    private void trainControl() {
        isStopped = false;
        stopTrainControlSystem = false;

        long timeout = 1000L / 2;

        long buffer = 0L;
        try {
            while (!stopTrainControlSystem) {
                long start = System.currentTimeMillis();

                // INFO: Update here
                this.update();

                long toWait = (timeout - buffer) - (System.currentTimeMillis() - start);

                if (toWait < 0) buffer = -toWait;
                else {
                    Thread.sleep(toWait);
                    buffer = 0L;
                }
            }
        } catch (InterruptedException e) {
            Ref.LOGGER.info("Stop updates");
        }
        isStopped = true;
    }

    @Override
    public String toString() {
        return "Railroad{" +
                "railroadElements=" + Arrays.toString(railroadElements) +
                ", stopTrainControlSystem=" + stopTrainControlSystem +
                ", isStopped=" + isStopped +
                '}';
    }
}
