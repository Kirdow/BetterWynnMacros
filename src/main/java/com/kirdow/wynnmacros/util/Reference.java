package com.kirdow.wynnmacros.util;

public class Reference {

    public static final boolean isDev;

    static {
        isDev = Boolean.parseBoolean(System.getProperty("wynnmacros.isDev", "false"));
    }

}
