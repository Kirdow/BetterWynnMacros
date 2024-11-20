package com.kirdow.wynnmacros.util;

import java.io.BufferedWriter;
import java.io.IOException;

public class Utils {

    public static IFormatWriter genWriter(final BufferedWriter writer) {
        return (format, args) -> {
            String text = String.format(format, args);

            writer.write(String.format("%s\n", text));
        };
    }

    public interface IFormatWriter {
        void writeLn(String format, Object...args) throws IOException;
    }

}
