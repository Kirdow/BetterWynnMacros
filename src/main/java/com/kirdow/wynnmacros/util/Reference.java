package com.kirdow.wynnmacros.util;

import com.kirdow.wynnmacros.Logger;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Reference {

    public static final boolean isDev;

    static {
        isDev = Boolean.parseBoolean(System.getProperty("wynnmacros.isDev", "false"));
    }

}
