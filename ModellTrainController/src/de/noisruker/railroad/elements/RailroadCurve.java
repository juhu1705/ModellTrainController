package de.noisruker.railroad.elements;

import de.noisruker.gui.RailroadImages;
import de.noisruker.loconet.messages.AbstractMessage;
import de.noisruker.railroad.Position;
import de.noisruker.railroad.RailRotation;
import de.noisruker.util.Util;
import javafx.scene.image.Image;

import java.io.BufferedWriter;
import java.io.IOException;

public class RailroadCurve extends AbstractRailroadElement {

    public RailroadCurve(RailRotation rotation, Position position) {
        super("curve", position, rotation);
    }

    @Override
    public Image getImage() {
        switch (rotation) {
            case NORTH:
                return RailroadImages.CURVE_NORTH_EAST;
            case WEST:
                return RailroadImages.CURVE_NORTH_WEST;
            case EAST:
                return RailroadImages.CURVE_SOUTH_EAST;
            case SOUTH:
                return RailroadImages.CURVE_SOUTH_WEST;
        }
        return RailroadImages.EMPTY_2;
    }

    @Override
    public void onLocoNetMessage(AbstractMessage message) {
    }

    @Override
    public Position getToPos(Position from) {
        switch (rotation) {
            case NORTH:
                if (from.equals(new Position(position.getX(), position.getY() - 1)))
                    return new Position(position.getX() + 1, position.getY());
                else if (from.equals(new Position(position.getX() + 1, position.getY())))
                    return new Position(position.getX(), position.getY() - 1);
                break;
            case SOUTH:
                if (from.equals(new Position(position.getX(), position.getY() + 1)))
                    return new Position(position.getX() - 1, position.getY());
                else if (from.equals(new Position(position.getX() - 1, position.getY())))
                    return new Position(position.getX(), position.getY() + 1);
                break;
            case WEST:
                if (from.equals(new Position(position.getX() - 1, position.getY())))
                    return new Position(position.getX(), position.getY() - 1);
                else if (from.equals(new Position(position.getX(), position.getY() - 1)))
                    return new Position(position.getX() - 1, position.getY());
                break;
            case EAST:
                if (from.equals(new Position(position.getX(), position.getY() + 1)))
                    return new Position(position.getX() + 1, position.getY());
                else if (from.equals(new Position(position.getX() + 1, position.getY())))
                    return new Position(position.getX(), position.getY() + 1);
                break;
        }
        return null;
    }

    @Override
    public void saveTo(BufferedWriter writer) throws IOException {
        super.saveTo(writer);
        Util.closeWriting();
    }
}
