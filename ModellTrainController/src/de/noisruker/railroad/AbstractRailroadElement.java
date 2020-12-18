package de.noisruker.railroad;

import de.noisruker.loconet.messages.AbstractMessage;
import de.noisruker.util.SaveAble;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public abstract class AbstractRailroadElement implements SaveAble {

    private static final ArrayList<AbstractRailroadElement> RAILROAD_ELEMENTS = new ArrayList<>();

    public static void sendToElements(AbstractMessage message) {
        RAILROAD_ELEMENTS.forEach(e -> e.onLocoNetMessage(message));
    }

    private final String elementName;

    public AbstractRailroadElement(String elementName) {
        this.elementName = elementName;
        RAILROAD_ELEMENTS.add(this);
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

    public void onRemoveElement() {
        RAILROAD_ELEMENTS.remove(this);
    }

}
