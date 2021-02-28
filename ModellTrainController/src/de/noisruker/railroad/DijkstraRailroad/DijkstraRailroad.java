package de.noisruker.railroad.DijkstraRailroad;

import de.noisruker.railroad.elements.AbstractRailroadElement;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.railroad.elements.Switch;

import java.util.ArrayList;

import static de.noisruker.railroad.DijkstraRailroad.SwitchNodePosition.*;

public class DijkstraRailroad {

    public void convertRailroad(AbstractRailroadElement[][] railroad) {
        DijkstraNode.reset();

        ArrayList<Switch> switches = new ArrayList<>();

        for(AbstractRailroadElement[] elements: railroad)
            for(AbstractRailroadElement element: elements)
                if(element instanceof Switch)
                    switches.add((Switch) element);

        for(Switch s: switches) {
            this.createNodesFor(s);
        }
    }

    private void createNodesFor(Switch s) {
        DijkstraNode inSwitch = new DijkstraNode(s, WAY_BOTH);
        DijkstraNode outSwitch1 = new DijkstraNode(s, OUTPUT);
        DijkstraNode outSwitch2 = new DijkstraNode(s, OUTPUT);

        inSwitch.addToTrue(outSwitch1);
        inSwitch.addToFalse(outSwitch2);

        DijkstraNode outSwitch = new DijkstraNode(s, OUTPUT);
        DijkstraNode inSwitch1 = new DijkstraNode(s, WAY_TRUE);
        DijkstraNode inSwitch2 = new DijkstraNode(s, WAY_FALSE);

        inSwitch1.addToTrue(outSwitch);
        inSwitch2.addToFalse(outSwitch);
    }


}
