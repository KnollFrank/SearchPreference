package com.bytehamster.lib.preferencesearch;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import java.util.List;

public class PreferenceParser {

    private final PreferenceFragmentHelper preferenceFragmentHelper;

    public PreferenceParser(final PreferenceFragmentHelper preferenceFragmentHelper) {
        this.preferenceFragmentHelper = preferenceFragmentHelper;
    }

    public List<Preference> parsePreferenceScreen(final Class<? extends PreferenceFragmentCompat> preferenceScreen) {
        return getPreferences(preferenceScreen);
    }

    public static List<Preference> getPreferences(final PreferenceGroup preferenceGroup) {
        final Builder<Preference> preferencesBuilder = ImmutableList.builder();
        for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
            final Preference preference = preferenceGroup.getPreference(i);
            preferencesBuilder.add(preference);
            if (preference instanceof PreferenceGroup) {
                preferencesBuilder.addAll(getPreferences((PreferenceGroup) preference));
            }
        }
        return preferencesBuilder.build();
    }

    private List<Preference> getPreferences(final Class<? extends PreferenceFragmentCompat> preferenceScreen) {
        return getPreferences(getPreferenceScreen(preferenceScreen));
    }

    private PreferenceScreen getPreferenceScreen(final Class<? extends PreferenceFragmentCompat> resId) {
        return this
                .preferenceFragmentHelper
                .getPreferenceScreenOfFragment(resId.getName())
                .get()
                .preferenceScreen;
    }
}
