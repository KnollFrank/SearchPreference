package de.KnollFrank.lib.preferencesearch.search;

import android.content.Context;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import java.util.List;
import java.util.stream.Collectors;

import de.KnollFrank.lib.preferencesearch.search.provider.SearchableInfoAttribute;
import de.KnollFrank.lib.preferencesearch.search.provider.SummarySetter;

class SearchAndDisplay {

    private final PreferenceSearcher preferenceSearcher;
    private final SummarySetter summarySetter;
    private final SearchableInfoAttribute searchableInfoAttribute;
    private final PreferenceScreen preferenceScreen;
    private final Context context;

    public SearchAndDisplay(final PreferenceSearcher preferenceSearcher,
                            final SummarySetter summarySetter,
                            final SearchableInfoAttribute searchableInfoAttribute,
                            final PreferenceScreen preferenceScreen,
                            final Context context) {
        this.preferenceSearcher = preferenceSearcher;
        this.summarySetter = summarySetter;
        this.searchableInfoAttribute = searchableInfoAttribute;
        this.preferenceScreen = preferenceScreen;
        this.context = context;
    }

    public void searchForQueryAndDisplayResults(final String query) {
        final List<PreferenceMatch> preferenceMatches = preferenceSearcher.searchFor(query);
        display(preferenceMatches);
    }

    private void display(final List<PreferenceMatch> preferenceMatches) {
        PreferenceMatchesHighlighter.highlight(
                preferenceMatches,
                summarySetter,
                searchableInfoAttribute,
                context);
        PreferenceVisibility.makePreferencesOfPreferenceScreenVisible(
                getPreferences(preferenceMatches),
                preferenceScreen);
    }

    private static List<Preference> getPreferences(final List<PreferenceMatch> preferenceMatches) {
        return preferenceMatches
                .stream()
                .map(preferenceMatch -> preferenceMatch.preference)
                .collect(Collectors.toList());
    }
}
