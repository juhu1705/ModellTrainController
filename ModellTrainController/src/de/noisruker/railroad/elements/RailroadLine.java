package de.noisruker.railroad.elements;

import de.noisruker.gui.RailroadImages;
import de.noisruker.loconet.messages.AbstractMessage;
import de.noisruker.railroad.Position;
import de.noisruker.railroad.RailRotation;
import de.noisruker.util.Util;
import javafx.scene.image.Image;

import java.io.BufferedWriter;
import java.io.IOException;

public class RailroadLine extends AbstractRailroadElement {

    public RailroadLine(RailRotation rotation, Position position) {
        super("track", position, rotation);
    }

    @Override
    public Image getImage() {
        return rotation.equals(RailRotation.NORTH) || rotation.equals(RailRotation.SOUTH) ? RailroadImages.STRAIGHT_VERTICAL : RailroadImages.STRAIGHT_HORIZONTAL;
    }

    @Override
    public void onLocoNetMessage(AbstractMessage message) {
    }

    @Override
    public Position getToPos(Position from) {
        switch (rotation) {
            case NORTH:
            case SOUTH:
                if (from.equals(new Position(position.getX(), position.getY() + 1)))
                    return new Position(position.getX(), position.getY() - 1);
                else if (from.equals(new Position(position.getX(), position.getY() - 1)))
                    return new Position(position.getX(), position.getY() + 1);
                break;
            case WEST:
            case EAST:
                if (from.equals(new Position(position.getX() - 1, position.getY())))
                    return new Position(position.getX() + 1, position.getY());
                else if (from.equals(new Position(position.getX() + 1, position.getY())))
                    return new Position(position.getX() - 1, position.getY());
                break;
        }


        return null;
    }

    @Override
    public void saveTo(BufferedWriter writer) throws IOException {
        super.saveTo(writer);
        Util.closeWriting();
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
