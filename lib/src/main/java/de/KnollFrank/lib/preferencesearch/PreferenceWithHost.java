package de.KnollFrank.lib.preferencesearch;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class PreferenceWithHost implements IPreferenceItem {

    public final Preference preference;
    public final Class<? extends PreferenceFragmentCompat> host;

    public PreferenceWithHost(final Preference preference,
                              final Class<? extends PreferenceFragmentCompat> host) {
        this.preference = preference;
        this.host = host;
    }

    @Override
    public boolean matches(final String keyword) {
        return PreferenceItems
                .getPreferenceItem(preference)
                .matches(keyword);
    }

    @Override
    public String toString() {
        return "PreferenceWithHost{" +
                "preference=" + preference +
                ", host=" + host +
                '}';
    }
}
