package de.noisruker.railroad.elements;

import de.noisruker.gui.RailroadImages;
import de.noisruker.loconet.messages.AbstractMessage;
import de.noisruker.railroad.Position;
import de.noisruker.railroad.RailRotation;
import de.noisruker.util.Util;
import javafx.scene.image.Image;

import java.io.BufferedWriter;
import java.io.IOException;

public class Crossing extends AbstractRailroadElement {

    public Crossing(Position position, RailRotation rotation) {
        super("crossing", position, rotation);
    }

    @Override
    public Image getImage() {
        return RailroadImages.CROSSING;
    }

    @Override
    public void onLocoNetMessage(AbstractMessage message) {

    }

    @Override
    public void saveTo(BufferedWriter writer) throws IOException {
        super.saveTo(writer);
        Util.closeWriting();
    }

    @Override
    public Position getToPos(Position from) {
        if(from.equals(new Position(this.getPosition().getX() + 1, this.getPosition().getY())))
            return new Position(this.getPosition().getX() - 1, this.getPosition().getY());
        else if(from.equals(new Position(this.getPosition().getX() - 1, this.getPosition().getY())))
            return new Position(this.getPosition().getX() + 1, this.getPosition().getY());
        else if(from.equals(new Position(this.getPosition().getX(), this.getPosition().getY() - 1)))
            return new Position(this.getPosition().getX(), this.getPosition().getY() + 1);
        else if(from.equals(new Position(this.getPosition().getX(), this.getPosition().getY() + 1)))
            return new Position(this.getPosition().getX(), this.getPosition().getY() - 1);
        return null;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
