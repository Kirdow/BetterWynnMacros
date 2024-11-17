package com.kirdow.wynnmacros;

import com.kirdow.wynnmacros.util.Reference;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Logger {
    private static Consumer<String> loggerInfo, loggerDebug, loggerError, loggerWarn;
    static void setLogger(Consumer<String> info, Consumer<String> debug, Consumer<String> error, Consumer<String> warn) {
        loggerInfo = info;
        loggerDebug = debug;
        loggerError = error;
        loggerWarn = warn;
    }

    private static final int THROTTLE_LIMIT_AGE = 0;
    private static final Set<ThrottledMessage> throttleSet = new HashSet<>();

    private static void refreshThrottleCache() {
        throttleSet.removeIf(p -> !p.validAge());
    }

    private static boolean throttle(String message) {
        refreshThrottleCache();
        var msg = new ThrottledMessage(message);
        if (throttleSet.contains(msg)) return true;
        throttleSet.add(msg);
        return false;
    }

    public static void info(String format, Object...args) {
        log(String.format(format, args), loggerInfo);
    }

    public static void dev(String format, Object...args) {
        if (!Reference.isDev) return;
        String str = String.format(format, args);
        log(String.format("DEV: %s", str), loggerInfo);
    }

    public static void debug(String format, Object...args) {
        log(String.format(format, args), loggerDebug);
    }

    public static void error(String format, Object...args) {
        log(String.format(format, args), loggerError);
    }

    public static void warn(String format, Object...args) {
        log(String.format(format, args), loggerWarn);
    }

    private static void log(String msg, Consumer<String> func) {
        if (throttle(msg)) return;
        func.accept(msg);
    }

    private static class ThrottledMessage {
        public final int hashCode;
        public final long time;

        public ThrottledMessage(String message) {
            hashCode = 31 * 17 + message.hashCode();
            time = System.currentTimeMillis();
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof ThrottledMessage other)) return false;
            return other.hashCode == hashCode;
        }

        public boolean validAge() {
            return System.currentTimeMillis() < time + THROTTLE_LIMIT_AGE;
        }
    }

}
