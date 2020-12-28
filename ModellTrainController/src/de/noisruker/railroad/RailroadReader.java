package de.noisruker.railroad;

import de.noisruker.gui.GuiMain;
import de.noisruker.railroad.elements.*;
import de.noisruker.util.Ref;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.HashMap;

public class RailroadReader implements ContentHandler {

    private static HashMap<String, Reader> readers = new HashMap<>();

    public static void registerReader(String key, Reader reader) {
        readers.put(key, reader);
    }

    protected static void readParams(String forType, HashMap<String, String> params) {
        if(readers.containsKey(forType))
            readers.get(forType).read(params);
    }

    private HashMap<String, String> params = new HashMap<>();
    private String current = "", type, parName = "", parValue = "";
    private static AbstractRailroadElement[][] railroad = new AbstractRailroadElement[100][100];

    @Override
    public void setDocumentLocator(Locator locator) {

    }

    @Override
    public void startDocument() throws SAXException {

    }

    @Override
    public void endDocument() throws SAXException {
        if(type != null) {
            RailroadReader.readParams(type, params);
            params.clear();
            type = null;
        }
        GuiMain.getInstance().applyRailroad(railroad);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {

    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(localName.equals("element")) {
            if(type != null) {
                RailroadReader.readParams(type, params);
                params.clear();
                type = null;
            }
        } else if(localName.equals("type"))
            type = current;
        else {
            this.params.put(localName, current);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        current = new String(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {

    }

    @Override
    public void skippedEntity(String name) throws SAXException {

    }

    public interface Reader {
        void read(HashMap<String, String> params);
    }

    static {
        registerReader("switch", params -> {
            if(areAllKeysContained(params, "posX", "posY", "address", "rotation", "normal_state", "switch_type")){
                railroad[Integer.parseInt(params.get("posX"))][Integer.parseInt(params.get("posY"))] = new Switch(
                        Byte.parseByte(params.get("address")), Switch.SwitchType.valueOf(params.get("switch_type")),
                        RailRotation.valueOf(params.get("rotation")), Boolean.parseBoolean(params.get("normal_state")),
                        new Position(Integer.parseInt(params.get("posX")), Integer.parseInt(params.get("posY"))));
            }
        });
        registerReader("track", params -> {
            if(areAllKeysContained(params, "posX", "posY", "rotation"))
                railroad[Integer.parseInt(params.get("posX"))][Integer.parseInt(params.get("posY"))] = new RailroadLine(
                        RailRotation.valueOf(params.get("rotation")),
                        new Position(Integer.parseInt(params.get("posX")), Integer.parseInt(params.get("posY"))));
        });
        registerReader("curve", params -> {
            if(areAllKeysContained(params, "posX", "posY", "rotation"))
                railroad[Integer.parseInt(params.get("posX"))][Integer.parseInt(params.get("posY"))] = new RailroadCurve(
                        RailRotation.valueOf(params.get("rotation")),
                        new Position(Integer.parseInt(params.get("posX")), Integer.parseInt(params.get("posY"))));
        });
        registerReader("directional", params -> {
            if(areAllKeysContained(params, "posX", "posY", "rotation"))
                railroad[Integer.parseInt(params.get("posX"))][Integer.parseInt(params.get("posY"))] = new RailroadDirectionalLine(
                        RailRotation.valueOf(params.get("rotation")),
                        new Position(Integer.parseInt(params.get("posX")), Integer.parseInt(params.get("posY"))));
        });
        registerReader("ending", params -> {
            if(areAllKeysContained(params, "posX", "posY", "rotation"))
                railroad[Integer.parseInt(params.get("posX"))][Integer.parseInt(params.get("posY"))] = new RailroadEnd(
                        RailRotation.valueOf(params.get("rotation")),
                        new Position(Integer.parseInt(params.get("posX")), Integer.parseInt(params.get("posY"))));
        });
        registerReader("sensor", params -> {
            if(areAllKeysContained(params, "posX", "posY", "rotation", "address"))
                railroad[Integer.parseInt(params.get("posX"))][Integer.parseInt(params.get("posY"))] = new Sensor(
                        Byte.parseByte(params.get("address")), false,
                        new Position(Integer.parseInt(params.get("posX")), Integer.parseInt(params.get("posY"))),
                        RailRotation.valueOf(params.get("rotation")));
        });
    }

    public static boolean areAllKeysContained(HashMap<String, String> params, String... keys) {
        for(String key: keys) {
            if(!params.containsKey(key))
                return false;
        }
        return true;
    }
}
