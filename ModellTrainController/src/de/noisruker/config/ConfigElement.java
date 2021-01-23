package de.noisruker.config;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Diese Annotation verifiziert ein Attribut, als Konfigurationsattribut. Dabei
 * muss das Attribut {@code public static}, also �ffentlich und statisch sein.
 * Das Config Elements wird �ber die Klasse {@link ConfigManager Konfigurations
 * Manager} registriert. Konfigurationsattribute, die ein {@code String},
 * {@code Integer}, oder {@code Boolean} als Wert aufweisen, werden automatisch
 * im GUI unter dem Reiter Einstellungen zu finden sein. Dabei ist
 * {@link #description() die Beschreibung} als Hovertext und {@link #name() der
 * Name} als Benennung eingef�gt. Hierbei werden diese beiden eingegebenen
 * Strings durch den String aus der verwendeten Sprachdatei ersetzt. Alle
 * registrierten Konfigurationselemente werden in der Config-Datei unter
 * "%localappdata%/CaRP/config.cfg" gespeichert.
 *
 * @author Juhu1705
 * @version 1.0
 * @category Config
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface ConfigElement {

    /**
     * @return Den Standartm��ig gesetzte Initialwert.
     * @implNote Nur Strings werden automatisch richtig initialisiert. Bitte den
     * gew�nschten Wert standartm��ig einprogrammieren. Dieser wird
     * �berschrieben, sobald die Konfigurationsdatei geladen wird.
     */
    String defaultValue();

    /**
     * @return Den Objekttypen dieser Klasse. Wenn prim�re Datentypen wie int
     * benutzt werden, dann kann hier die Klasse Integer.class verwendet
     * werden.
     */
    String type();

    /**
     * @return Den in den Sprachdateien hinterlegten �bersetzungsstring f�r die
     * Beschreibung der Konfiguration
     * @implNote Der hinterlegte String muss, damit dass Programm l�uft, in den
     * Sprachdateien hinterlegt sein.
     */
    String description();

    /**
     * @return Den in den Sprachdateien hinterlegten Key zum �bersetzten des Namens.
     * @implNote Der hinterlegte String muss, damit dass Programm l�uft in den
     * Sprachdateien hinterlegt sein.
     */
    String name();

    /**
     * @return Die Position im Baumsystem, unter der die Config zu finden ist.
     * @implNote Der hinterlegte String muss, damit dass Programm l�uft in den
     * Sprachdateien hinterlegt sein. Ein "." trennt die Strings. Jeder
     * Einzelstring wird mit "String".location gesucht.
     */
    String location();

    boolean visible();

}