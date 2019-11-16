package de.noisruker.net.datapackets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation, die an Methoden angehängt werden muss, die {@linkplain Datapacket
 * Datenpakete} abfangen sollen
 * 
 * @author Niklas
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NetEventHandler {

	DatapacketType type();
}
