package de.KnollFrank.lib.settingssearch.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Lists {

    public static <T> List<T> concat(final List<List<T>> lists) {
        return lists
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public static <T> List<T> concat(final List<T>... lists) {
        return concat(Arrays.asList(lists));
    }

    public static <T> List<T> asList(final Optional<T[]> elements) {
        return elements
                .map(Arrays::asList)
                .orElseGet(Collections::emptyList);
    }

    public static <T> List<T> getPresentElements(final List<Optional<T>> elements) {
        return elements
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static <T> Optional<T> getLastElement(final List<T> ts) {
        if (ts.isEmpty()) {
            return Optional.empty();
        }
        final T lastElement = ts.get(ts.size() - 1);
        return Optional.of(lastElement);
    }
}
