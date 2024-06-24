package de.KnollFrank.lib.preferencesearch.search;

import android.os.Bundle;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.KnollFrank.lib.preferencesearch.MergedPreferenceScreen;
import de.KnollFrank.lib.preferencesearch.Navigation;
import de.KnollFrank.lib.preferencesearch.PreferenceScreenWithHostProvider;
import de.KnollFrank.lib.preferencesearch.PreferenceScreensProvider;
import de.KnollFrank.lib.preferencesearch.R;
import de.KnollFrank.lib.preferencesearch.SearchConfigurations;
import de.KnollFrank.lib.preferencesearch.client.SearchConfiguration;
import de.KnollFrank.lib.preferencesearch.common.Keyboard;
import de.KnollFrank.lib.preferencesearch.common.Preferences;
import de.KnollFrank.lib.preferencesearch.fragment.FragmentFactory;
import de.KnollFrank.lib.preferencesearch.fragment.Fragments;
import de.KnollFrank.lib.preferencesearch.fragment.FragmentsFactory;
import de.KnollFrank.lib.preferencesearch.fragment.IFragmentFactory;
import de.KnollFrank.lib.preferencesearch.provider.MergedPreferenceScreenProvider;
import de.KnollFrank.lib.preferencesearch.provider.MergedPreferenceScreenProviderListener;
import de.KnollFrank.lib.preferencesearch.provider.PreferenceScreensMerger;
import de.KnollFrank.lib.preferencesearch.results.SearchResultsPreferenceFragment;

public class SearchPreferenceFragment extends Fragment {

    private final MergedPreferenceScreenProviderListener mergedPreferenceScreenProviderListener;
    private final IFragmentFactory fragmentFactory;
    private SearchConfiguration searchConfiguration;

    public static SearchPreferenceFragment newInstance(final SearchConfiguration searchConfiguration,
                                                       // FK-TODO: delete MergedPreferenceScreenProviderListener
                                                       final MergedPreferenceScreenProviderListener mergedPreferenceScreenProviderListener,
                                                       final IFragmentFactory fragmentFactory) {
        final SearchPreferenceFragment searchPreferenceFragment = new SearchPreferenceFragment(mergedPreferenceScreenProviderListener, fragmentFactory);
        searchPreferenceFragment.setArguments(SearchConfigurations.toBundle(searchConfiguration));
        return searchPreferenceFragment;
    }

    public SearchPreferenceFragment(final MergedPreferenceScreenProviderListener mergedPreferenceScreenProviderListener,
                                    final IFragmentFactory fragmentFactory) {
        super(R.layout.searchpreference_fragment);
        this.mergedPreferenceScreenProviderListener = mergedPreferenceScreenProviderListener;
        this.fragmentFactory = fragmentFactory;
    }

    public SearchPreferenceFragment() {
        this(
                new MergedPreferenceScreenProviderListener() {

                    @Override
                    public void onStartGetMergedPreferenceScreen(final String preferenceFragment) {
                    }

                    @Override
                    public void onFinishGetMergedPreferenceScreen(final String preferenceFragment) {
                    }
                },
                new FragmentFactory());
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchConfiguration = SearchConfigurations.fromBundle(requireArguments());
    }

    @Override
    public void onStart() {
        super.onStart();
        final MergedPreferenceScreen mergedPreferenceScreen = geMergedPreferenceScreen();
        showSearchResultsPreferenceFragment(mergedPreferenceScreen);
        configureSearchView(mergedPreferenceScreen);
    }

    private MergedPreferenceScreen geMergedPreferenceScreen() {
        final Fragments fragments =
                FragmentsFactory.createFragments(
                        fragmentFactory,
                        requireActivity(),
                        getChildFragmentManager(),
                        R.id.dummyFragmentContainerView);
        final MergedPreferenceScreenProvider mergedPreferenceScreenProvider =
                new MergedPreferenceScreenProvider(
                        fragments,
                        new PreferenceScreensProvider(new PreferenceScreenWithHostProvider(fragments)),
                        new PreferenceScreensMerger(getContext()),
                        mergedPreferenceScreenProviderListener);
        return mergedPreferenceScreenProvider.getMergedPreferenceScreen(searchConfiguration.rootPreferenceFragment.getName());
    }

    private void showSearchResultsPreferenceFragment(final MergedPreferenceScreen mergedPreferenceScreen) {
        Navigation.show(
                SearchResultsPreferenceFragment.newInstance(
                        searchConfiguration.fragmentContainerViewId,
                        mergedPreferenceScreen),
                false,
                getChildFragmentManager(),
                R.id.searchResultsFragmentContainerView,
                true);
    }

    private void configureSearchView(final MergedPreferenceScreen mergedPreferenceScreen) {
        final SearchView searchView = requireView().findViewById(R.id.searchView);
        SearchViewConfigurer.configureSearchView(
                searchView,
                searchConfiguration.textHint,
                new SearchAndDisplay(
                        new PreferenceSearcher(Preferences.getAllPreferences(mergedPreferenceScreen.preferenceScreen)),
                        mergedPreferenceScreen.preferenceScreen,
                        requireContext()));
        selectSearchView(searchView);
        searchView.setQuery(searchView.getQuery(), true);
    }

    private void selectSearchView(final SearchView searchView) {
        searchView.requestFocus();
        Keyboard.showKeyboard(getActivity(), searchView);
    }
}
