package de.KnollFrank.lib.preferencesearch.common;

import org.threeten.bp.Duration;

import java.util.concurrent.TimeUnit;

public class Utils {

    public static <T> Class<? extends T> getClass(final String className) {
        try {
            return (Class<? extends T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void sleep(final Duration duration) {
        try {
            TimeUnit.MILLISECONDS.sleep(duration.toMillis());
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
