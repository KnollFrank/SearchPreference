package de.KnollFrank.lib.settingssearch;

import androidx.preference.Preference;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import de.KnollFrank.lib.settingssearch.common.Lists;

public class PreferencePath {

    public final List<Preference> preferences;

    public PreferencePath(final List<Preference> preferences) {
        this.preferences = preferences;
    }

    public Optional<Preference> getPreference() {
        return Lists.getLastElement(preferences);
    }

    public PreferencePath add(final Preference preference) {
        return new PreferencePath(
                ImmutableList
                        .<Preference>builder()
                        .addAll(preferences)
                        .add(preference)
                        .build());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PreferencePath that = (PreferencePath) o;
        return Objects.equals(preferences, that.preferences);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(preferences);
    }

    @Override
    public String toString() {
        return "PreferencePath{" +
                "preferences=" + preferences +
                '}';
    }
}
