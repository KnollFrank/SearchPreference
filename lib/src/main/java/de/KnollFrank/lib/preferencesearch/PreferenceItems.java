package de.KnollFrank.lib.preferencesearch;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class PreferenceItems {

    public static List<PreferenceItem> getPreferenceItems(
            final List<Preference> preferences,
            final Class<? extends PreferenceFragmentCompat> resId) {
        return preferences
                .stream()
                .map(preference -> getPreferenceItem(preference, resId))
                .collect(Collectors.toList());
    }

    public static PreferenceItem getPreferenceItem(
            final Preference preference,
            final Class<? extends PreferenceFragmentCompat> resId) {
        class _PreferenceItems {

            public static PreferenceItem getPreferenceItem(final Preference preference,
                                                           final Class<? extends PreferenceFragmentCompat> resId) {
                return new PreferenceItem(
                        asString(preference.getTitle()),
                        asString(preference.getSummary()),
                        Optional.ofNullable(preference.getKey()),
                        Optional.empty(),
                        Optional.empty(),
                        getEntries(preference),
                        resId);
            }

            private static Optional<String> asString(final CharSequence charSequence) {
                return Optional
                        .ofNullable(charSequence)
                        .map(CharSequence::toString);
            }

            private static Optional<String> getEntries(final Preference preference) {
                if (!(preference instanceof ListPreference)) {
                    return Optional.empty();
                }
                return Optional
                        .ofNullable(((ListPreference) preference).getEntries())
                        .map(Arrays::toString);
            }
        }

        return _PreferenceItems.getPreferenceItem(preference, resId);
    }
}
