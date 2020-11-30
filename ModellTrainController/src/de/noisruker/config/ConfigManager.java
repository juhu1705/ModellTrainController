package de.noisruker.config;

import static de.noisruker.util.Ref.LOGGER;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.noisruker.util.Ref;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * Hier werden alle {@link ConfigElement Konfigurations Elemente} gespeichert
 * und verwaltet. Diese Klasse erm�glicht das Laden der initialisierten Werte,
 * einer Konfigurations-Datei, sowie das Schreiben einer Konfigurationsdatei.
 * 
 * @author Juhu1705
 * @category Config
 * @version 1.0
 */
public class ConfigManager {

	/**
	 * Die Instanz des {@link ConfigManager Konfigurations Managers}.
	 */
	private static ConfigManager instance;

	/**
	 * @return Die {@link #instance aktive Instanz} des Konfigurations-Managers.
	 */
	public static ConfigManager getInstance() {
		return instance == null ? instance = new ConfigManager() : instance;
	}

	/**
	 * Alle {@link ConfigElement Konfigurations-Elemente}, die registriert wurden.
	 */
	private final ArrayList<Field> fields = new ArrayList<>();

	/**
	 * Gibt das erste {@code Field} aus {@link #fields der Liste aller
	 * Konfigurations-Elemente} aus, dessen Name mit dem mitgegebenen {@link String}
	 * �bereinstimmt zur�ck.
	 * 
	 * @param name Die Bennenung des {@link ConfigElement Konfigurations-Element}
	 * @return Das gleichnamige {@link ConfigElement Konfigurations-Element}, oder
	 *         {@code null}, falls keines Vorhanden.
	 */
	public Field getField(String name) {
		for (Field f : fields) {
			if (f.getName().equals(name))
				return f;
		}
		return null;
	}

	/**
	 * F�gt das zu registrierende {@link ConfigElement} in {@link #fields die Liste
	 * aller Konfigurations-Elemente} ein, wenn es �ber die Annotation
	 * {@link ConfigElement} verf�gt.
	 * 
	 * 
	 * @param configElement Das zu registrierende {@link ConfigElement}
	 * @throws IOException Sollte die Datei, die zu registrieren versucht wird,
	 *                     nicht die Annotation {@link ConfigElement} besitzen, wird
	 *                     eine IOException mit der Nachricht: "Not the right
	 *                     annotation argument.", ausgegeben.
	 */
	public void register(Field configElement) throws IOException {
		if (configElement.getAnnotation(ConfigElement.class) == null)
			throw new IOException("Not the right annotation argument.");
		fields.add(configElement);
	}

	/**
	 * Registriert alle {@link Field Felder} der {@link Class Klasse}, die �ber die
	 * Annotation {@link ConfigElement} verf�gen �ber die Methode
	 * {@link #register(Field)}.
	 * 
	 * @param c Die {@link Class Klasse}, deren {@link Field Felder}, welche die
	 *          Annotation {@link ConfigElement} tragen, registriert werden sollen.
	 * @throws IOException Sollte ein Fehler beim Registrieren der Felder auftreten.
	 */
	public void register(Class c) throws IOException {
		for (Field f : c.getFields()) {

			if (f.getAnnotation(ConfigElement.class) != null)
				this.register(f);

		}
	}

	/**
	 * L�dt die unter {@link ConfigElement#defaultValue() dem Standartwert}
	 * mitgegebenen Werte in die jeweiligen Felder, sollten diese nicht
	 * standartm��ig �ber einen Wert verf�gen.
	 * 
	 * @implNote Diese Methode Funktioniert nur bedingt. Daher ist es ratsam, die
	 *           Felder direkt zu initialisieren.
	 */
	public void loadDefault() {
		this.fields.forEach(r -> {
			boolean a = r.isAccessible();
			r.setAccessible(true);
			try {
				if (r.get(this) == null) {
					ConfigElement e = r.getAnnotation(ConfigElement.class);
					String dv = e.defaultValue();

					if (e.type().equals("check"))
						r.set(this, Boolean.parseBoolean(dv));
					else if (e.type().equals("count"))
						r.set(this, Integer.parseInt(dv));
					else if (e.type().equals("text"))
						r.set(this, dv);
					else
						r.set(this, dv);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			r.setAccessible(a);
		});
	}

	/**
	 * <p>
	 * L�dt eine Konfigurations Datei ein. Dabei werden die {@link #fields Elemente
	 * aus der Liste aller Konfigurations-Elemente} auf den f�r sie vermerkten Wert
	 * gesetzt.
	 * </p>
	 * 
	 * <p>
	 * Die Konfigurationsdatei ist in {@code XML} zu schreiben. Dabei umschlie�t der
	 * Parameter {@code config} die gesammte Konfigurationsdatei. Unter dem
	 * Parameter {@code fields} k�nnen mithilfe des Parameters {@code field} und dem
	 * folgenden Parameter {@code parameter} die einzelnen Konfigurationswerte
	 * gesetzt werden. Dabei ist {@code name} der Name des zu setztenden Feldes.
	 * {@code value} gibt den Wert an, auf den es gesetzt wird. Unter
	 * {@code default} kann der Standartwert als orientierung angegeben werden und
	 * unter {@code type} ist die Klasse vermerkt, in welche das {@code value}
	 * konvertiert wird.
	 * </p>
	 * 
	 * @param input Der Pfad zu der einzulesenden Datei.
	 * @throws SAXException Sollte ein Fehler in der Struktur der Datei vorliegen.
	 * @throws IOException  Sollte ein Fehler beim Einlesen der Datei aufgtreten
	 */
	public void load(String input) throws SAXException, IOException {

		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		InputSource inputSource = new InputSource(new FileReader(input));

		xmlReader.setContentHandler(new FieldHandler());
		xmlReader.parse(inputSource);

	}

	/**
	 * Exportiert die Daten der {@link #fields Elemente aus der Liste aller
	 * Konfigurations-Elemente} in eine Datei nach dem unter {@link #load(String)}
	 * erkl�rten Aufbau. Diese Datei ist von der Methode {@link #load(String)}
	 * wieder einlesbar.
	 * 
	 * @param output Der Pfad zu dem Exportiert wird.
	 * @throws IOException Sollte es nicht m�glixh sein an den angegebenen Pfad zu
	 *                     Schreiben, oder sollte output gleich {@code null} sein
	 */
	public void save(File output) throws IOException {
		if (output == null)
			throw new IOException("No file to write to!");

		FileWriter fw;
		BufferedWriter bw;

		fw = new FileWriter(output);
		bw = new BufferedWriter(fw);

		bw.append("<config>");
		bw.newLine();
		bw.append(" <fields>");
		bw.newLine();

		fields.forEach(e -> {
			try {
				bw.append("  <field>");
				bw.newLine();

				bw.append("   <parameter>");
				bw.newLine();

				bw.append("    <name>" + e.getName() + "</name>");
				bw.newLine();
				bw.append("    <value>" + e.get(this) + "</value>");
				bw.newLine();
				bw.append("    <default>" + e.getAnnotation(ConfigElement.class).defaultValue() + "</default>");
				bw.newLine();
				bw.append("    <type>" + e.getAnnotation(ConfigElement.class).type() + "</type>");
				bw.newLine();

				bw.append("   </parameter>");
				bw.newLine();

				bw.append("  </field>");
				bw.newLine();
			} catch (IllegalArgumentException | IOException | IllegalAccessException e1) {
				LOGGER.log(Level.SEVERE, "Fehler beim Erstellen der Config datei!", e1);
			}
		});

		bw.append(" </fields>");
		bw.newLine();
		bw.append(" <theme>");
		bw.newLine();

		bw.append("   <themetype>" + Ref.theme.name() + "</themetype>");
		bw.newLine();

		bw.append(" </theme>");
		bw.newLine();

		bw.append("</config>");

		bw.close();

	}

	public void createMenuTree(TreeView<String> tree, VBox configurations) {
		CheckBoxTreeItem root = new CheckBoxTreeItem(Ref.language.getString("config.location"));

		tree.setCellFactory(CheckBoxTreeCell.forTreeView());

		root.setExpanded(true);

		tree.setRoot(root);

		for (Field f : this.fields) {
			if (f.getAnnotation(ConfigElement.class) == null)
				continue;
			ConfigElement e = f.getAnnotation(ConfigElement.class);

			CheckBoxTreeItem actual = null;

			for (String s : e.location().split("\\.")) {
				if (actual == null) {
					actual = root;
					continue;
				}
				boolean found = false;
				for (Object ti : actual.getChildren()) {
					if (((String) ((TreeItem) ti).getValue())
							.equalsIgnoreCase(Ref.language.getString("config.location." + s))) {
						actual = (CheckBoxTreeItem) ti;
						found = true;
						break;
					}
				}
				if (!found) {
					TreeItem nti = new CheckBoxTreeItem<String>(Ref.language.getString("config.location." + s));

					nti.setExpanded(true);
					actual.getChildren().add(0, nti);
					actual = (CheckBoxTreeItem) nti;
				}
			}

			if (e.type().equals("check")) {
				CheckBoxTreeItem cb = new CheckBoxTreeItem(Ref.language.getString("config." + e.name()));
//				cb.setTooltip(new Tooltip(References.language.getString(e.description())));
				try {
					cb.setSelected(f.getBoolean(null));

				} catch (IllegalArgumentException | IllegalAccessException e2) {
					e2.printStackTrace();
				}
				this.listeners.add(new ChangeEntry(e.name(), () -> {
					try {
						if(cb.isSelected() != f.getBoolean(null))
							cb.setSelected(f.getBoolean(null));
					} catch (IllegalArgumentException | IllegalAccessException ignored) {

					}
				}));


				cb.selectedProperty().addListener((obs, oldValue, newValue) -> {
					try {
						if(oldValue != newValue) {
							f.setBoolean(null, newValue);
							this.onConfigChanged(e.name());
						}
					} catch (IllegalArgumentException | IllegalAccessException e1) {
						LOGGER.severe("Error while updating config!");
					}
				});

				cb.addEventHandler(ActionEvent.ACTION, event -> {
					try {
						f.setBoolean(null, cb.isSelected());
					} catch (IllegalArgumentException e1) {
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					}
				});
//				configurationTree.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
				cb.setExpanded(true);
				actual.getChildren().add(cb);
			}

		}

		tree.addEventHandler(MouseEvent.MOUSE_CLICKED, (event -> {
			CheckBoxTreeItem selected = (CheckBoxTreeItem) tree.getSelectionModel().getSelectedItem();

			if (selected == null)
				return;
			String location = (String) selected.getValue();

			CheckBoxTreeItem<String> actual = selected;

			while (actual != null) {

				actual = (CheckBoxTreeItem<String>) actual.getParent();

				if (actual != null)
					location = actual.getValue() + "." + location;

			}

			if (selected != null) {
				configurations.getChildren().clear();
				for (Field f : this.fields) {

					if (f.getAnnotation(ConfigElement.class) == null)
						continue;
					ConfigElement e = f.getAnnotation(ConfigElement.class);

					String fieldlocation = "";

					for (String s : e.location().split("\\."))
						if (!s.isEmpty())
							fieldlocation = fieldlocation + (fieldlocation == "" ? "" : ".")
									+ Ref.language.getString("config.location." + s);

					if (location.equalsIgnoreCase(fieldlocation + "." + Ref.language.getString("config." + e.name()))) {
						TextArea ta = new TextArea(Ref.language.getString("config." + e.description()));

						ta.setEditable(false);
						ta.setWrapText(true);

						configurations.getChildren()
								.addAll(new Label(Ref.language.getString("label.description") + ":"), ta);
					}

					if (!fieldlocation.equalsIgnoreCase(location))
						continue;

					if (e.type().equals("count")) {
						Spinner cb = new Spinner();
						cb.setTooltip(new Tooltip(Ref.language.getString("config." + e.description())));
						cb.setEditable(true);
						this.listeners.add(new ChangeEntry(e.name(), () -> {
							try {
								cb.getValueFactory().setValue(f.getInt(null));
							} catch (IllegalArgumentException | IllegalAccessException ignored) {

							}
						}));
						try {
							if (this.maxCounting.containsKey(e.name()))
								cb.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,
										this.maxCounting.get(e.name()), f.getInt(null)));
							else
								cb.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,
										Integer.MAX_VALUE, f.getInt(null)));
							cb.getValueFactory().valueProperty().addListener((o, oldValue, newValue) -> {
								try {
									if(oldValue != newValue) {
										f.set(null, newValue);
										this.onConfigChanged(e.name());
									}

								} catch (IllegalArgumentException e1) {
									e1.printStackTrace();
								} catch (IllegalAccessException e1) {
									e1.printStackTrace();
								}
							});
						} catch (IllegalArgumentException | IllegalAccessException e3) {
							e3.printStackTrace();
						}

						Label l = new Label((Ref.language.getString("config." + e.name()) + ":"));

						l.autosize();
						configurations.getChildren().addAll(l, cb);
					} else if (e.type().equals("text")) {

						TextField cb = new TextField();
						cb.setTooltip(new Tooltip(Ref.language.getString("config." + e.description())));

						try {
							cb.setText((String) f.get(null));
						} catch (IllegalArgumentException | IllegalAccessException e2) {
							e2.printStackTrace();
						}

						this.listeners.add(new ChangeEntry(e.name(), () -> {
							try {
								cb.setText((String) f.get(null));
							} catch (IllegalArgumentException | IllegalAccessException ignored) {

							}
						}));

						cb.addEventHandler(KeyEvent.KEY_RELEASED, events -> {
							try {
								f.set(null, cb.getText());
								this.onConfigChanged(e.name());
							} catch (IllegalArgumentException | IllegalAccessException e1) {
								e1.printStackTrace();
							}
						});
						Label l = new Label((Ref.language.getString("config." + e.name()) + ":"));

						l.autosize();

						configurations.getChildren().addAll(l, cb);
					} else if (e.type().equals("choose") && this.options.containsKey(e.name())) {

						ComboBox<String> cb = new ComboBox<String>();
						cb.setTooltip(new Tooltip(Ref.language.getString("config." + e.description())));

						cb.setItems(FXCollections.observableArrayList(this.options.get(e.name()).options()));

						try {
							cb.setValue((String) f.get(null));
						} catch (IllegalArgumentException | IllegalAccessException e2) {
							e2.printStackTrace();
						}

						this.listeners.add(new ChangeEntry(e.name(), () -> {
							try {
								cb.setValue((String) f.get(null));
							} catch (IllegalArgumentException | IllegalAccessException ignored) {

							}
						}));

						cb.addEventHandler(ActionEvent.ANY, events -> {
							try {
								f.set(null, cb.getValue());
								this.onConfigChanged(e.name());
							} catch (IllegalArgumentException | IllegalAccessException e1) {
								e1.printStackTrace();
							}
						});
						Label l = new Label((Ref.language.getString("config." + e.name()) + ":"));

						l.autosize();

						configurations.getChildren().addAll(l, cb);
					}

				}
			}
		}));
	}

	private final HashMap<String, Integer> maxCounting = new HashMap<>();
	private final HashMap<String, GetOptions> options = new HashMap<>();

	public void registerIntegerMax(String s, int max) {
		this.maxCounting.put(s, max);
	}

	public void registerOptionParameters(String s, GetOptions options) {
		this.options.put(s + ".text", options);
	}

	public void onConfigChanged(String fieldName) {
		for(ChangeEntry e: listeners)
			if(e.getForValue().equals(fieldName))
				e.getListener().onChange();
	}

	ArrayList<ChangeEntry> listeners = new ArrayList<>();

    public void registerActionListener(String s, ActionListener listener) {
		listeners.add(new ChangeEntry(s + ".text", listener));
    }

    private class ChangeEntry {
    	private final String s;
    	private final ActionListener l;

    	public ChangeEntry(String s, ActionListener l) {
    		this.s = s;
    		this.l = l;
		}

		public String getForValue() {
    		return s;
		}

		public ActionListener getListener() {
    		return l;
		}
	}

	public void onConfigChangedGeneral() {
		for (Field f : this.fields) {
			if (f.getAnnotation(ConfigElement.class) == null)
				continue;
			ConfigElement e = f.getAnnotation(ConfigElement.class);
			this.onConfigChanged(e.name());
		}
	}

	public interface ActionListener {
		void onChange();
	}

    public interface GetOptions {
		ArrayList<String> options();
	}

}
