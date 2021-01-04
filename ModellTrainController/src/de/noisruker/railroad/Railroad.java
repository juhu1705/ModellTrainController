package de.noisruker.railroad;

import de.noisruker.gui.GuiMain;
import de.noisruker.loconet.messages.SensorMessage;
import de.noisruker.loconet.messages.SwitchMessage;
import de.noisruker.loconet.LocoNet;
import de.noisruker.loconet.LocoNetMessageReceiver;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.util.Ref;
import javafx.application.Platform;
import javafx.geometry.Pos;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Railroad {

    private AbstractRailroadElement[][] railroadElements = null;

    public void applyRailroad(final AbstractRailroadElement[][] railroad) {
        if(railroad.length < 100)
            return;
        this.railroadElements = railroad;
        if(GuiMain.getInstance() != null)
            GuiMain.getInstance().checkOutRailroad();
    }

    public Railroad() { }

    public AbstractRailroadElement[][] getRailroad() {
        return railroadElements;
    }

    public void init() {
        LocoNetMessageReceiver.getInstance().registerListener(l -> {
            if (l instanceof SensorMessage) {
                SensorMessage s = (SensorMessage) l;

                Ref.LOGGER.info("Sensor " + s.getAddress() + "; State: " + s.getState());

                if(s.getState())
                    trainEnter(s.getAddress());
                else
                    trainLeft(s.getAddress());
            }
        });
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
        for(Train t: LocoNet.getInstance().getTrains()) {
            if(t.destination != null)
                counter++;
        }
        return counter;
    }

    public Railway findWay(Sensor from, Sensor to, Position dir) {
        findWay(from, to, dir);
        return new Railway(from.getPosition(), to.getPosition(), dir);
    }

    public void trainEnter(final int nodeAddress) {
        LocoNet.getInstance().getTrains().forEach(t -> t.trainEnter(nodeAddress));
    }

    public void trainLeft(final int nodeAddress) {
        LocoNet.getInstance().getTrains().forEach(t -> t.trainLeft(nodeAddress));
    }

    public void update() {
        LocoNet.getInstance().getTrains().forEach(Train::update);
    }

    private boolean stopTrainControlSystem = true;

    public void startTrainControlSystem() {
        if(stopTrainControlSystem)
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
        while(!stopTrainControlSystem) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) { }
            this.update();
        }
        isStopped = true;
    }

}
