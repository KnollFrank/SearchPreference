package de.KnollFrank.lib.preferencesearch.search;

import static de.KnollFrank.lib.preferencesearch.PreferenceWithHostList.getPreferences;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.KnollFrank.lib.preferencesearch.Navigation;
import de.KnollFrank.lib.preferencesearch.PreferenceFragments;
import de.KnollFrank.lib.preferencesearch.PreferenceScreenWithHosts;
import de.KnollFrank.lib.preferencesearch.PreferenceScreensProvider;
import de.KnollFrank.lib.preferencesearch.PreferencesProvider;
import de.KnollFrank.lib.preferencesearch.R;
import de.KnollFrank.lib.preferencesearch.SearchConfiguration;
import de.KnollFrank.lib.preferencesearch.SearchConfigurations;
import de.KnollFrank.lib.preferencesearch.common.Keyboard;
import de.KnollFrank.lib.preferencesearch.results.SearchResultsPreferenceFragment;

public class SearchPreferenceFragment extends Fragment {

    private SearchConfiguration searchConfiguration;

    public static SearchPreferenceFragment newInstance(final SearchConfiguration searchConfiguration) {
        final SearchPreferenceFragment searchPreferenceFragment = new SearchPreferenceFragment();
        searchPreferenceFragment.setArguments(SearchConfigurations.toBundle(searchConfiguration));
        return searchPreferenceFragment;
    }

    public SearchPreferenceFragment() {
        super(R.layout.searchpreference_fragment);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchConfiguration = SearchConfigurations.fromBundle(requireArguments());
    }

    @Override
    public void onStart() {
        super.onStart();
        final View view = requireView();
        final PreferenceScreenWithHosts preferenceScreenWithHosts =
                this
                        .getPreferencesProvider(R.id.dummyFragmentContainerView)
                        .getPreferenceScreenWithHosts();
        final SearchResultsPreferenceFragment searchResultsPreferenceFragment =
                SearchResultsPreferenceFragment.newInstance(
                        searchConfiguration.fragmentContainerViewId,
                        preferenceScreenWithHosts);
        Navigation.show(
                searchResultsPreferenceFragment,
                false,
                getChildFragmentManager(),
                R.id.searchResultsFragmentContainerView,
                true);
        {
            final SearchView searchView = view.findViewById(R.id.searchView);
            SearchViewConfigurer.configureSearchView(
                    searchView,
                    searchConfiguration.textHint,
                    new SearchAndDisplay(
                            new PreferenceSearcher(getPreferences(preferenceScreenWithHosts.preferenceWithHostList)),
                            preferenceScreenWithHosts.preferenceScreen));
            selectSearchView(searchView);
            searchView.setQuery(searchView.getQuery(), true);
        }
    }

    private void selectSearchView(final SearchView searchView) {
        searchView.requestFocus();
        Keyboard.showKeyboard(getActivity(), searchView);
    }

    private PreferencesProvider getPreferencesProvider(final @IdRes int fragmentContainerViewId) {
        return new PreferencesProvider(
                searchConfiguration.rootPreferenceFragment.getName(),
                new PreferenceScreensProvider(
                        new PreferenceFragments(
                                requireActivity(),
                                getChildFragmentManager(),
                                fragmentContainerViewId)),
                getContext());
    }
}
