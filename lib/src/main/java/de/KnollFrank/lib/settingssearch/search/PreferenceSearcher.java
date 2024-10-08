package de.KnollFrank.lib.settingssearch.search;

import static de.KnollFrank.lib.settingssearch.search.Summaries4MatchingSearchableInfosAdapter.showSearchableInfosOfPreferencesIfQueryMatchesSearchableInfo;

import java.util.List;
import java.util.stream.Collectors;

import de.KnollFrank.lib.settingssearch.MergedPreferenceScreen;
import de.KnollFrank.lib.settingssearch.common.Lists;
import de.KnollFrank.lib.settingssearch.common.Preferences;
import de.KnollFrank.lib.settingssearch.search.provider.SearchableInfoAttribute;
import de.KnollFrank.lib.settingssearch.search.provider.SearchableInfoProvider;

class PreferenceSearcher {

    private final MergedPreferenceScreen mergedPreferenceScreen;
    private final SearchableInfoAttribute searchableInfoAttribute;
    private final SearchableInfoProvider searchableInfoProvider;

    public PreferenceSearcher(final MergedPreferenceScreen mergedPreferenceScreen,
                              final SearchableInfoAttribute searchableInfoAttribute,
                              final SearchableInfoProvider searchableInfoProvider) {
        this.mergedPreferenceScreen = mergedPreferenceScreen;
        this.searchableInfoAttribute = searchableInfoAttribute;
        this.searchableInfoProvider = searchableInfoProvider;
    }

    public List<PreferenceMatch> searchFor(final String needle) {
        prepareSearch(needle);
        return getPreferenceMatches(needle);
    }

    private void prepareSearch(final String needle) {
        mergedPreferenceScreen.resetPreferenceScreen();
        showSearchableInfosOfPreferencesIfQueryMatchesSearchableInfo(
                mergedPreferenceScreen.preferenceScreen,
                searchableInfoProvider,
                searchableInfoAttribute,
                needle);
    }

    private List<PreferenceMatch> getPreferenceMatches(final String needle) {
        return Lists.concat(
                Preferences
                        .getAllPreferences(mergedPreferenceScreen.preferenceScreen)
                        .stream()
                        .map(preference ->
                                PreferenceMatcher.getPreferenceMatches(
                                        preference,
                                        needle,
                                        searchableInfoAttribute))
                        .collect(Collectors.toList()));
    }
}
