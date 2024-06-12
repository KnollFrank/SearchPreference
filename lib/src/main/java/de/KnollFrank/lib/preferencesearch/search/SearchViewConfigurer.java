package de.KnollFrank.lib.preferencesearch.search;

import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import de.KnollFrank.lib.preferencesearch.PreferenceWithHost;
import de.KnollFrank.lib.preferencesearch.SearchConfiguration;
import de.KnollFrank.lib.preferencesearch.results.SearchResultsPreferenceFragment;

class SearchViewConfigurer {

    public static void configureSearchView(final SearchView searchView,
                                           final SearchResultsPreferenceFragment searchResultsPreferenceFragment,
                                           final PreferenceSearcher<PreferenceWithHost> preferenceSearcher,
                                           final SearchConfiguration searchConfiguration) {
        searchConfiguration.textHint.ifPresent(searchView::setQueryHint);
        searchView.setOnQueryTextListener(
                createOnQueryTextListener(
                        searchResultsPreferenceFragment,
                        preferenceSearcher));
    }

    private static OnQueryTextListener createOnQueryTextListener(
            final SearchResultsPreferenceFragment searchResultsPreferenceFragment,
            final PreferenceSearcher<PreferenceWithHost> preferenceSearcher) {
        return new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {
                onQueryTextChange(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                filterPreferenceItemsBy(newText);
                return true;
            }

            private void filterPreferenceItemsBy(final String query) {
                searchResultsPreferenceFragment.setPreferenceWithHostList(
                        preferenceSearcher.searchFor(query));
            }
        };
    }
}
