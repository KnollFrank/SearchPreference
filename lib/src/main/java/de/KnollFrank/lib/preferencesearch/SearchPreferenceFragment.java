package de.KnollFrank.lib.preferencesearch;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import de.KnollFrank.lib.preferencesearch.common.UIUtils;

public class SearchPreferenceFragment extends Fragment {

    @IdRes
    private static final int FRAGMENT_CONTAINER_VIEW = R.id.fragmentContainerView2;

    private SearchConfiguration searchConfiguration;

    public SearchPreferenceFragment() {
        super(R.layout.searchpreference_fragment);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchConfiguration = SearchConfigurations.fromBundle(getArguments());
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        @IdRes final int dummyFragmentContainerViewId =
                UIUtils
                        .createAndAddFragmentContainerView2ViewGroup((ViewGroup) view, getContext())
                        .getId();
        final List<PreferenceWithHost> preferenceWithHostList =
                this
                        .getPreferencesProvider(dummyFragmentContainerViewId)
                        .getPreferences();
        final SearchResultsPreferenceFragment searchResultsPreferenceFragment = new SearchResultsPreferenceFragment();
        {
            final SearchView searchView = view.findViewById(R.id.searchView);
            configureSearchView(
                    searchView,
                    searchResultsPreferenceFragment,
                    new PreferenceSearcher<>(preferenceWithHostList),
                    searchConfiguration);
            selectSearchView(searchView);
        }
        if (savedInstanceState == null) {
            searchResultsPreferenceFragment.setPreferenceWithHostList(preferenceWithHostList);
            Navigation.show(
                    searchResultsPreferenceFragment,
                    false,
                    getChildFragmentManager(),
                    FRAGMENT_CONTAINER_VIEW);
        }
    }

    private static void configureSearchView(final SearchView searchView,
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

    // FK-TODO: DRY with SearchPreferenceFragment
    private void selectSearchView(final SearchView searchView) {
        searchView.requestFocus();
        showKeyboard(searchView);
    }

    // FK-TODO: DRY with SearchPreferenceFragment
    private void showKeyboard(final View view) {
        final InputMethodManager inputMethodManager = getInputMethodManager();
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    // FK-TODO: DRY with SearchPreferenceFragment
    private InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private IPreferencesProvider<PreferenceWithHost> getPreferencesProvider(final int fragmentContainerViewId) {
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
