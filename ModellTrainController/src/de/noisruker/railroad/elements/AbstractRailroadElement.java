package de.noisruker.railroad.elements;

import de.noisruker.loconet.messages.AbstractMessage;
import de.noisruker.railroad.Position;
import de.noisruker.railroad.RailRotation;
import de.noisruker.util.SaveAble;
import de.noisruker.util.Util;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This class represents a single Railroad element.
 *
 * Every Railroad Element has a Position and a rotation.
 */
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

    /**
     * Creates a new AbstractRailroad Element
     *
     * @param elementName The identifier name (If this railroad is a switch type "switch", if it is a sensor type "sensor")
     * @param position The position of the element
     * @param rotation The rotation of the elemen
     */
    public AbstractRailroadElement(String elementName, Position position, RailRotation rotation) {
        this.elementName = elementName;
        this.position = position;
        this.rotation = rotation;
        RAILROAD_ELEMENTS.add(this);
    }

    /**
     * Saves the element to the {@link BufferedWriter writer}
     *
     * @param writer The buffer to write to
     * @throws IOException If any error while writing the data to the buffer appears
     * @implNote Please use the {@link Util#writeParameterToBuffer(String, String)} to write a parameter and the
     * {@link Util#closeWriting()} if you have finished the writing.
     * Also call this method before you writing your own stuff.
     *
     * To read your element from the file back register a reader to the {@link de.noisruker.railroad.RailroadReader} with this elements key
     */
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

    public Position getPosition() {
        return this.position;
    }

    public void onRemoveElement() {
        RAILROAD_ELEMENTS.remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractRailroadElement element = (AbstractRailroadElement) o;
        return position.equals(element.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }

    public RailRotation getRotation() {
        return this.rotation;
    }

    @Override
    public String toString() {
        return elementName + "{" +
                position +
                '}';
    }
}
