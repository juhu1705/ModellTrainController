package de.noisruker.railroad.elements;

import de.noisruker.gui.RailroadImages;
import de.noisruker.loconet.messages.AbstractMessage;
import de.noisruker.railroad.Position;
import de.noisruker.railroad.RailRotation;
import de.noisruker.util.Util;
import javafx.scene.image.Image;

import java.io.BufferedWriter;
import java.io.IOException;

public class RailroadDirectionalLine extends AbstractRailroadElement {

    public RailroadDirectionalLine(RailRotation rotation, Position position) {
        super("directional", position, rotation);
    }

    @Override
    public Image getImage() {
        switch (rotation) {
            case NORTH:
                return RailroadImages.STRAIGHT_NORTH;
            case WEST:
                return RailroadImages.STRAIGHT_WEST;
            case EAST:
                return RailroadImages.STRAIGHT_EAST;
            case SOUTH:
                return RailroadImages.STRAIGHT_SOUTH;
        }
        return null;
    }

    @Override
    public void onLocoNetMessage(AbstractMessage message) { }

    @Override
    public Position getToPos(Position from) {
        switch (rotation) {
            case NORTH:
                return new Position(position.getX(), position.getY() - 1);
            case SOUTH:
                return new Position(position.getX(), position.getY() + 1);
            case WEST:
                return new Position(position.getX() - 1, position.getY());
            case EAST:
                return new Position(position.getX() + 1, position.getY());
        }
        return null;
    }

    @Override
    public void saveTo(BufferedWriter writer) throws IOException {
        super.saveTo(writer);
        Util.closeWriting();
    }
}
