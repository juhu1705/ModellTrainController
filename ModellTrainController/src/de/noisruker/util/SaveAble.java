package de.noisruker.util;

import java.io.BufferedWriter;
import java.io.IOException;

public interface SaveAble {

    public void saveTo(BufferedWriter writer) throws IOException;

    public String getSaveString();

}
