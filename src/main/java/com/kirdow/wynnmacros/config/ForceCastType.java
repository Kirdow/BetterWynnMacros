package com.kirdow.wynnmacros.config;

import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ForceCastType {

    OFF("None"),
    WAIT("Wait For Timeout"),
    BLOCKING("Block Manual");

    private final String title;

    ForceCastType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getDisplay() {
        return String.format("%s%s", this == OFF ? Formatting.RED : Formatting.GREEN, title);
    }

    public int getId() {
        return ordinal();
    }

    public ForceCastType next() {
        return getById(ordinal() + 1);
    }

    public ForceCastType prev() {
        return getById(ordinal() - 1);
    }

    public String toString() {
        return title;
    }

    public static ForceCastType getByTitle(String title) {
        return TITLE_MAP.getOrDefault(title, null);
    }

    public static ForceCastType getById(int id) {
        var v = values();
        return v[(id + v.length) % v.length];
    }

    private static final Map<String, ForceCastType> TITLE_MAP;

    static {
        TITLE_MAP = new HashMap<>();
        Arrays.stream(values()).forEach(p -> TITLE_MAP.put(p.title, p));
    }

}
