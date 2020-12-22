package de.noisruker.railroad;

import de.noisruker.gui.RailroadImages;
import de.noisruker.loconet.messages.AbstractMessage;
import javafx.scene.image.Image;

import java.io.BufferedWriter;
import java.io.IOException;

public class RailroadLine extends AbstractRailroadElement {

    private final RailRotation rotation;

    public RailroadLine(RailRotation rotation, Position position) {
        super("track", position);
        this.rotation = rotation;
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
        return null;
    }

    @Override
    public void saveTo(BufferedWriter writer) throws IOException {

    }
}
