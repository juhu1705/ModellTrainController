package de.noisruker.railroad.DijkstraRailroad;

import java.util.ArrayList;
import java.util.Arrays;

public enum NodePosition {
    IN_TRUE, IN_FALSE, IN_BOTH, OUT_TRUE, OUT_FALSE, OUT_BOTH;

    public static ArrayList<NodePosition> switchablePositions = new ArrayList<NodePosition>(Arrays.asList(IN_TRUE, IN_FALSE, OUT_TRUE, OUT_FALSE));
}
