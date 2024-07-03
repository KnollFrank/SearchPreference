package de.KnollFrank.lib.preferencesearch.search;

import static de.KnollFrank.lib.preferencesearch.fragment.Fragments.showFragment;
import static de.KnollFrank.lib.preferencesearch.search.provider.BuiltinSearchableInfoProvidersFactory.createBuiltinSearchableInfoProviders;

import android.os.Bundle;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.function.Consumer;

import de.KnollFrank.lib.preferencesearch.MergedPreferenceScreen;
import de.KnollFrank.lib.preferencesearch.PreferenceScreenWithHostProvider;
import de.KnollFrank.lib.preferencesearch.PreferenceScreensProvider;
import de.KnollFrank.lib.preferencesearch.R;
import de.KnollFrank.lib.preferencesearch.SearchConfigurations;
import de.KnollFrank.lib.preferencesearch.client.SearchConfiguration;
import de.KnollFrank.lib.preferencesearch.common.Keyboard;
import de.KnollFrank.lib.preferencesearch.fragment.DefaultFragmentFactory;
import de.KnollFrank.lib.preferencesearch.fragment.FragmentFactory;
import de.KnollFrank.lib.preferencesearch.fragment.Fragments;
import de.KnollFrank.lib.preferencesearch.fragment.FragmentsFactory;
import de.KnollFrank.lib.preferencesearch.provider.MergedPreferenceScreenProvider;
import de.KnollFrank.lib.preferencesearch.provider.PreferenceScreensMerger;
import de.KnollFrank.lib.preferencesearch.provider.SearchablePreferencePredicate;
import de.KnollFrank.lib.preferencesearch.results.SearchResultsPreferenceFragment;
import de.KnollFrank.lib.preferencesearch.search.provider.ISearchableInfoProviderInternal;
import de.KnollFrank.lib.preferencesearch.search.provider.SearchableInfoProviderInternal;

public class SearchPreferenceFragment extends Fragment {

    private final SearchablePreferencePredicate searchablePreferencePredicate;
    private final ISearchableInfoProviderInternal searchableInfoProviderInternal;
    private final FragmentFactory fragmentFactory;
    private SearchConfiguration searchConfiguration;

    public static SearchPreferenceFragment newInstance(
            final SearchConfiguration searchConfiguration,
            final SearchablePreferencePredicate searchablePreferencePredicate,
            final ISearchableInfoProviderInternal searchableInfoProviderInternal,
            final FragmentFactory fragmentFactory) {
        final SearchPreferenceFragment searchPreferenceFragment =
                new SearchPreferenceFragment(
                        searchablePreferencePredicate,
                        searchableInfoProviderInternal,
                        fragmentFactory);
        searchPreferenceFragment.setArguments(SearchConfigurations.toBundle(searchConfiguration));
        return searchPreferenceFragment;
    }

    public SearchPreferenceFragment(final SearchablePreferencePredicate searchablePreferencePredicate,
                                    final ISearchableInfoProviderInternal searchableInfoProviderInternal,
                                    final FragmentFactory fragmentFactory) {
        super(R.layout.searchpreference_fragment);
        this.searchablePreferencePredicate = searchablePreferencePredicate;
        this.searchableInfoProviderInternal = searchableInfoProviderInternal;
        this.fragmentFactory = fragmentFactory;
    }

    public SearchPreferenceFragment() {
        this(
                (preference, host) -> true,
                new SearchableInfoProviderInternal(createBuiltinSearchableInfoProviders()),
                new DefaultFragmentFactory());
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchConfiguration = SearchConfigurations.fromBundle(requireArguments());
    }

    @Override
    public void onStart() {
        super.onStart();
        final MergedPreferenceScreen mergedPreferenceScreen = getMergedPreferenceScreen();
        showSearchResultsPreferenceFragment(
                mergedPreferenceScreen,
                searchResultsPreferenceFragment -> configureSearchView(mergedPreferenceScreen));
    }

    private MergedPreferenceScreen getMergedPreferenceScreen() {
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
                        searchablePreferencePredicate,
                        true);
        return mergedPreferenceScreenProvider.getMergedPreferenceScreen(searchConfiguration.rootPreferenceFragment.getName());
    }

    private void showSearchResultsPreferenceFragment(final MergedPreferenceScreen mergedPreferenceScreen,
                                                     final Consumer<SearchResultsPreferenceFragment> onFragmentStarted) {
        showFragment(
                SearchResultsPreferenceFragment.newInstance(
                        searchConfiguration.fragmentContainerViewId,
                        mergedPreferenceScreen),
                onFragmentStarted,
                false,
                R.id.searchResultsFragmentContainerView,
                getChildFragmentManager());
    }

    private void configureSearchView(final MergedPreferenceScreen mergedPreferenceScreen) {
        final SearchView searchView = requireView().findViewById(R.id.searchView);
        SearchViewConfigurer.configureSearchView(
                searchView,
                searchConfiguration.textHint,
                new SearchAndDisplay(
                        PreferenceSearcher.createPreferenceSearcher(
                                mergedPreferenceScreen,
                                searchableInfoProviderInternal),
                        mergedPreferenceScreen.summaryResetterByPreference,
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
