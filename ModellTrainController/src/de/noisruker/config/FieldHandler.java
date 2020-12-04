package de.noisruker.config;

import java.lang.reflect.Field;
import java.util.logging.Level;

import de.noisruker.util.Ref;
import de.noisruker.util.Theme;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Diese Klasse organisiert das Einlesen der Config Datei.
 * 
 * @author Juhu1705
 * @category Config
 * @version 1.0
 * @see ContentHandler
 * @see ConfigManager#load(String)
 */
public class FieldHandler implements ContentHandler {

	/**
	 * Zwischenspeicherung f�r die einzulesenden Daten.
	 */
	private String value, defaultValue, type, name, currentValue;

	/**
	 * Speichert die eingelesenden Werte zur Bearbeitung.
	 */
	@Override
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		currentValue = new String(arg0, arg1, arg2);
	}

	@Override
	public void endDocument() throws SAXException {

	}

	/**
	 * L�dt die Daten in das Programm.
	 */
	@Override
	public void endElement(String arg0, String arg1, String arg2) throws SAXException {
		if (arg1.equals("name"))
			name = currentValue;
		if (arg1.equals("value"))
			value = currentValue;
		if (arg1.equals("default"))
			defaultValue = currentValue;
		if (arg1.equals("type"))
			type = currentValue;

		if (arg1.equals("field")) {
			Field f = ConfigManager.getInstance().getField(name);
			if (f == null)
				return;

			boolean a = f.isAccessible();
			f.setAccessible(true);
			try {

				ConfigElement e = f.getAnnotation(ConfigElement.class);

				if (e.type().equals("count"))
					f.set(this, Integer.parseInt(value));
				else if (e.type().equals("check"))
					f.set(this, Boolean.parseBoolean(value));
				else
					f.set(this, value);

				ConfigManager.getInstance().onConfigChanged(e.name());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			f.setAccessible(a);
		}

	}

	@Override
	public void endPrefixMapping(String arg0) throws SAXException {

	}

	@Override
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {

	}

	@Override
	public void processingInstruction(String arg0, String arg1) throws SAXException {

	}

	@Override
	public void setDocumentLocator(Locator arg0) {

	}

	@Override
	public void skippedEntity(String arg0) throws SAXException {

	}

	@Override
	public void startDocument() throws SAXException {

	}

	@Override
	public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException {

	}

	@Override
	public void startPrefixMapping(String arg0, String arg1) throws SAXException {

	}

}
