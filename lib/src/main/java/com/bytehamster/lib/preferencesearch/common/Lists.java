package com.bytehamster.lib.preferencesearch.common;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Lists {

    public static <T> List<T> concat(final List<List<T>> listOfLists) {
        return listOfLists
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public static <T> Optional<T> getFirstElement(final List<T> ts) {
        return getElement(ts, 0);
    }

    public static <T> Optional<T> getLastElement(final List<T> ts) {
        return getElement(ts, ts.size() - 1);
    }

    private static <T> Optional<T> getElement(final List<T> ts, final int index) {
        return 0 <= index && index < ts.size() ? Optional.of(ts.get(index)) : Optional.empty();
    }
}
