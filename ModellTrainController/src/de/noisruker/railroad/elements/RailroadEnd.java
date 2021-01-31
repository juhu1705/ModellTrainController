package de.noisruker.railroad.elements;

import de.noisruker.gui.RailroadImages;
import de.noisruker.loconet.messages.AbstractMessage;
import de.noisruker.railroad.Position;
import de.noisruker.railroad.RailRotation;
import de.noisruker.util.Util;
import javafx.scene.image.Image;

import java.io.BufferedWriter;
import java.io.IOException;

public class RailroadEnd extends AbstractRailroadElement {

    public RailroadEnd(RailRotation rotation, Position position) {
        super("ending", position, rotation);
    }

    @Override
    public Image getImage() {
        return switch (rotation) {
            case NORTH -> RailroadImages.END_NORTH;
            case WEST -> RailroadImages.END_WEST;
            case EAST -> RailroadImages.END_EAST;
            case SOUTH -> RailroadImages.END_SOUTH;
        };
    }

    @Override
    public void onLocoNetMessage(AbstractMessage message) {
    }

    @Override
    public Position getToPos(Position from) {
        return switch (rotation) {
            case NORTH -> new Position(position.getX(), position.getY() - 1);
            case SOUTH -> new Position(position.getX(), position.getY() + 1);
            case WEST -> new Position(position.getX() - 1, position.getY());
            case EAST -> new Position(position.getX() + 1, position.getY());
        };
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
