package de.KnollFrank.lib.preferencesearch.common;

import android.os.Bundle;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Bundles {

    private final Bundle bundle;

    public Bundles(final Bundle bundle) {
        this.bundle = bundle;
    }

    public void putOptionalString(final String key, final Optional<String> value) {
        value.ifPresent(_value -> bundle.putString(key, _value));
    }

    public Optional<String> getOptionalString(final String key) {
        return Optional.ofNullable(this.bundle.getString(key));
    }

    public List<String> getStringArrayList(final String key) {
        if (!this.bundle.containsKey(key)) {
            return Collections.emptyList();
        }
        final List<String> result = this.bundle.getStringArrayList(key);
        return result == null ? Collections.emptyList() : result;
    }

    public <T> void putClass(final String key, final Class<? extends T> value) {
        this.bundle.putString(key, value.getName());
    }

    public <T> Class<? extends T> getClass(final String key) {
        return Utils.getClass(this.bundle.getString(key));
    }
}
