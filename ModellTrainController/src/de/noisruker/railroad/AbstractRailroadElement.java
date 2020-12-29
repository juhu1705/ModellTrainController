package de.noisruker.railroad;

import com.sun.javafx.geom.Vec2d;
import de.noisruker.loconet.messages.AbstractMessage;
import de.noisruker.util.SaveAble;
import de.noisruker.util.Util;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public abstract class AbstractRailroadElement implements SaveAble {

    private static final ArrayList<AbstractRailroadElement> RAILROAD_ELEMENTS = new ArrayList<>();

    public static void sendToElements(AbstractMessage message) {
        RAILROAD_ELEMENTS.forEach(e -> e.onLocoNetMessage(message));
    }

    public static void clearElements() {
        RAILROAD_ELEMENTS.clear();
    }

    protected Position position;
    private final String elementName;
    protected final RailRotation rotation;

    public AbstractRailroadElement(String elementName, Position position, RailRotation rotation) {
        this.elementName = elementName;
        this.position = position;
        this.rotation = rotation;
        RAILROAD_ELEMENTS.add(this);
    }

    @Override
    public void saveTo(BufferedWriter writer) throws IOException {
        Util.setWriter(writer, elementName);
        Util.writeParameterToBuffer("posX", Integer.toString(position.getX()));
        Util.writeParameterToBuffer("posY", Integer.toString(position.getY()));
        Util.writeParameterToBuffer("rotation", rotation.name());
    }

    @Override
    public String getSaveString() {
        return this.elementName;
    }

    public abstract Image getImage();

    public void setImage(ImageView view) {
        view.setImage(this.getImage());
    }

    public abstract void onLocoNetMessage(AbstractMessage message);

    public abstract Position getToPos(Position from);

    public void onRemoveElement() {
        RAILROAD_ELEMENTS.remove(this);
    }
}
