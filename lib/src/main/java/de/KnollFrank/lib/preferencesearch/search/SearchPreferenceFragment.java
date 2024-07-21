package de.KnollFrank.lib.preferencesearch.search;

import static de.KnollFrank.lib.preferencesearch.fragment.Fragments.showFragment;

import android.os.Bundle;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.function.Consumer;

import de.KnollFrank.lib.preferencesearch.MergedPreferenceScreen;
import de.KnollFrank.lib.preferencesearch.PreferenceScreenWithHostProvider;
import de.KnollFrank.lib.preferencesearch.PreferenceScreensProvider;
import de.KnollFrank.lib.preferencesearch.R;
import de.KnollFrank.lib.preferencesearch.SearchConfigurations;
import de.KnollFrank.lib.preferencesearch.client.SearchConfiguration;
import de.KnollFrank.lib.preferencesearch.common.Keyboard;
import de.KnollFrank.lib.preferencesearch.common.Maps;
import de.KnollFrank.lib.preferencesearch.fragment.DefaultFragmentFactory;
import de.KnollFrank.lib.preferencesearch.fragment.FragmentFactory;
import de.KnollFrank.lib.preferencesearch.fragment.Fragments;
import de.KnollFrank.lib.preferencesearch.fragment.FragmentsFactory;
import de.KnollFrank.lib.preferencesearch.provider.MergedPreferenceScreenProvider;
import de.KnollFrank.lib.preferencesearch.provider.PreferenceScreensMerger;
import de.KnollFrank.lib.preferencesearch.provider.SearchablePreferencePredicate;
import de.KnollFrank.lib.preferencesearch.results.SearchResultsPreferenceFragment;
import de.KnollFrank.lib.preferencesearch.search.provider.PreferenceDescriptions;
import de.KnollFrank.lib.preferencesearch.search.provider.SearchableInfoAttribute;
import de.KnollFrank.lib.preferencesearch.search.provider.SearchableInfoProviderInternal;
import de.KnollFrank.lib.preferencesearch.search.provider.SearchableInfoProviders;

public class SearchPreferenceFragment extends Fragment {

    private final SearchablePreferencePredicate searchablePreferencePredicate;
    private final SearchableInfoProviders searchableInfoProviders;
    private final SearchableInfoAttribute searchableInfoAttribute;
    private final FragmentFactory fragmentFactory;
    private SearchConfiguration searchConfiguration;

    public static SearchPreferenceFragment newInstance(
            final SearchConfiguration searchConfiguration,
            final SearchablePreferencePredicate searchablePreferencePredicate,
            final SearchableInfoProviders searchableInfoProviders,
            final SearchableInfoAttribute searchableInfoAttribute,
            final FragmentFactory fragmentFactory) {
        final SearchPreferenceFragment searchPreferenceFragment =
                new SearchPreferenceFragment(
                        searchablePreferencePredicate,
                        searchableInfoProviders,
                        searchableInfoAttribute,
                        fragmentFactory);
        searchPreferenceFragment.setArguments(SearchConfigurations.toBundle(searchConfiguration));
        return searchPreferenceFragment;
    }

    public SearchPreferenceFragment(final SearchablePreferencePredicate searchablePreferencePredicate,
                                    final SearchableInfoProviders searchableInfoProviders,
                                    final SearchableInfoAttribute searchableInfoAttribute,
                                    final FragmentFactory fragmentFactory) {
        super(R.layout.searchpreference_fragment);
        this.searchablePreferencePredicate = searchablePreferencePredicate;
        this.searchableInfoProviders = searchableInfoProviders;
        this.searchableInfoAttribute = searchableInfoAttribute;
        this.fragmentFactory = fragmentFactory;
    }

    public SearchPreferenceFragment() {
        this(
                (preference, host) -> true,
                new SearchableInfoProviders(Collections.emptyMap()),
                new SearchableInfoAttribute(),
                new DefaultFragmentFactory());
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchConfiguration = SearchConfigurations.fromBundle(requireArguments());
    }

    @Override
    public void onResume() {
        super.onResume();
        final MergedPreferenceScreen mergedPreferenceScreen = getMergedPreferenceScreen();
        showSearchResultsPreferenceFragment(
                mergedPreferenceScreen,
                searchResultsPreferenceFragment -> configureSearchView(mergedPreferenceScreen));
    }

    private MergedPreferenceScreen getMergedPreferenceScreen() {
        final FragmentManager fragmentManager = getChildFragmentManager();
        final Fragments fragments =
                FragmentsFactory.createFragments(
                        fragmentFactory,
                        requireActivity(),
                        fragmentManager,
                        R.id.dummyFragmentContainerView);
        final MergedPreferenceScreenProvider mergedPreferenceScreenProvider =
                new MergedPreferenceScreenProvider(
                        fragmentManager,
                        fragments,
                        new PreferenceScreensProvider(new PreferenceScreenWithHostProvider(fragments)),
                        new PreferenceScreensMerger(getContext()),
                        searchablePreferencePredicate,
                        searchableInfoAttribute,
                        true);
        return mergedPreferenceScreenProvider.getMergedPreferenceScreen(searchConfiguration.rootPreferenceFragment.getName());
    }

    private void showSearchResultsPreferenceFragment(final MergedPreferenceScreen mergedPreferenceScreen,
                                                     final Consumer<SearchResultsPreferenceFragment> onFragmentStarted) {
        showFragment(
                SearchResultsPreferenceFragment.newInstance(
                        searchConfiguration.fragmentContainerViewId,
                        searchableInfoAttribute,
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
                        new PreferenceSearcher(
                                mergedPreferenceScreen,
                                searchableInfoAttribute,
                                getSearchableInfoProviderInternal(mergedPreferenceScreen)),
                        searchableInfoAttribute,
                        mergedPreferenceScreen.preferenceScreen,
                        requireContext()));
        selectSearchView(searchView);
        searchView.setQuery(searchView.getQuery(), true);
    }

    private SearchableInfoProviderInternal getSearchableInfoProviderInternal(final MergedPreferenceScreen mergedPreferenceScreen) {
        return new SearchableInfoProviderInternal(
                new SearchableInfoProviders(
                        Maps.merge(
                                ImmutableList.of(
                                        searchableInfoProviders.searchableInfoProvidersByPreferenceClass,
                                        PreferenceDescriptions.getSearchableInfoProvidersByPreferenceClass(mergedPreferenceScreen.getPreferenceDescriptions())))));
    }

    private void selectSearchView(final SearchView searchView) {
        searchView.requestFocus();
        Keyboard.showKeyboard(getActivity(), searchView);
    }
}
