package de.KnollFrank.lib.settingssearch.search;

import android.content.Context;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import java.util.List;
import java.util.stream.Collectors;

import de.KnollFrank.lib.settingssearch.search.provider.SearchableInfoAttribute;

class SearchAndDisplay {

    private final PreferenceSearcher preferenceSearcher;
    private final SearchableInfoAttribute searchableInfoAttribute;
    private final PreferenceScreen preferenceScreen;
    private final Context context;

    public SearchAndDisplay(final PreferenceSearcher preferenceSearcher,
                            final SearchableInfoAttribute searchableInfoAttribute,
                            final PreferenceScreen preferenceScreen,
                            final Context context) {
        this.preferenceSearcher = preferenceSearcher;
        this.searchableInfoAttribute = searchableInfoAttribute;
        this.preferenceScreen = preferenceScreen;
        this.context = context;
    }

    public void searchForQueryAndDisplayResults(final String query) {
        final List<PreferenceMatch> preferenceMatches = preferenceSearcher.searchFor(query);
        display(preferenceMatches);
    }

    private void display(final List<PreferenceMatch> preferenceMatches) {
        highlight(preferenceMatches);
        PreferenceVisibility.makePreferencesOfPreferenceScreenVisible(
                getPreferences(preferenceMatches),
                preferenceScreen);
    }

    private void highlight(final List<PreferenceMatch> preferenceMatches) {
        final PreferenceMatchesHighlighter preferenceMatchesHighlighter =
                new PreferenceMatchesHighlighter(
                        () -> MarkupFactory.createMarkups(context),
                        searchableInfoAttribute);
        preferenceMatchesHighlighter.highlight(preferenceMatches);
    }

    private static List<Preference> getPreferences(final List<PreferenceMatch> preferenceMatches) {
        return preferenceMatches
                .stream()
                .map(PreferenceMatch::preference)
                .collect(Collectors.toList());
    }
}
