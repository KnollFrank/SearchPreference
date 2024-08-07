package de.KnollFrank.lib.preferencesearch;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import de.KnollFrank.lib.preferencesearch.common.Maps;
import de.KnollFrank.lib.preferencesearch.search.PreferenceScreenResetter;
import de.KnollFrank.lib.preferencesearch.search.provider.PreferenceDescription;
import de.KnollFrank.lib.preferencesearch.search.provider.SearchableInfoAttribute;

public class MergedPreferenceScreen {

    public final PreferenceScreen preferenceScreen;
    private final Map<Preference, Class<? extends PreferenceFragmentCompat>> hostByPreference;
    private final Map<Preference, String> searchableInfoByPreference;
    public final Map<Preference, PreferencePath> preferencePathByPreference;
    private final PreferenceScreenResetter preferenceScreenResetter;

    public MergedPreferenceScreen(final PreferenceScreen preferenceScreen,
                                  final Map<Preference, Class<? extends PreferenceFragmentCompat>> hostByPreference,
                                  final Map<Preference, String> searchableInfoByPreference,
                                  final Map<Preference, PreferencePath> preferencePathByPreference,
                                  final SearchableInfoAttribute searchableInfoAttribute) {
        this.preferenceScreen = preferenceScreen;
        this.hostByPreference = hostByPreference;
        this.searchableInfoByPreference = searchableInfoByPreference;
        this.preferencePathByPreference = preferencePathByPreference;
        this.preferenceScreenResetter = new PreferenceScreenResetter(preferenceScreen, searchableInfoAttribute);
    }

    public Optional<? extends Class<? extends PreferenceFragmentCompat>> findHost(final Preference preference) {
        return Maps.get(hostByPreference, preference);
    }

    public List<PreferenceDescription> getPreferenceDescriptions() {
        return searchableInfoByPreference
                .keySet()
                .stream()
                .map(preference -> new PreferenceDescription<>(preference.getClass(), searchableInfoByPreference::get))
                .collect(Collectors.toList());
    }

    public void resetPreferenceScreen() {
        preferenceScreenResetter.reset();
    }
}
