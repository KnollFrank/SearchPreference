package com.bytehamster.lib.preferencesearch;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bytehamster.lib.preferencesearch.common.Lists;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class SearchPreferenceFragment extends Fragment implements SearchClickListener {

    public static final String TAG = "SearchPreferenceFragment";
    private List<PreferenceItem> preferenceItems;
    private SearchView searchView;
    private SearchConfiguration searchConfiguration;

    public SearchPreferenceFragment() {
        super(R.layout.searchpreference_fragment);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchConfiguration = SearchConfigurations.fromBundle(getArguments());
        // FK-TODO: preferenceItems hier berechnen, nicht nur auslesen?
        preferenceItems = PreferenceItemsBundle.readPreferenceItems(getArguments());
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = view.findViewById(R.id.searchView);
        final RecyclerView recyclerView = view.findViewById(R.id.list);
        final SearchPreferenceAdapter searchPreferenceAdapter =
                createAndConfigureSearchPreferenceAdapter(searchConfiguration, this);
        configureRecyclerView(recyclerView, searchPreferenceAdapter);
        configureSearchView(
                searchView,
                searchPreferenceAdapter,
                new PreferenceSearcher(preferenceItems),
                searchConfiguration);
        selectSearchView();
    }

    @Override
    public void onItemClicked(final PreferenceItem preferenceItem, final int position) {
        try {
            final SearchPreferenceResultListener searchPreferenceResultListener = (SearchPreferenceResultListener) getActivity();
            searchPreferenceResultListener.onSearchResultClicked(getSearchPreferenceResult(preferenceItem));
        } catch (final ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement SearchPreferenceResultListener");
        }
    }

    private static SearchPreferenceResult getSearchPreferenceResult(final PreferenceItem preferenceItem) {
        return new SearchPreferenceResult(
                preferenceItem.key,
                preferenceItem.resId,
                Lists
                        .getLastElement(preferenceItem.keyBreadcrumbs)
                        .orElse(null));
    }

    private static SearchPreferenceAdapter createAndConfigureSearchPreferenceAdapter(
            final SearchConfiguration searchConfiguration,
            final SearchClickListener onItemClickListener) {
        final SearchPreferenceAdapter searchPreferenceAdapter = new SearchPreferenceAdapter();
        searchPreferenceAdapter.setSearchConfiguration(searchConfiguration);
        searchPreferenceAdapter.setOnItemClickListener(onItemClickListener);
        return searchPreferenceAdapter;
    }

    private void selectSearchView() {
        searchView.requestFocus();
        showKeyboard(searchView);
    }

    private void showKeyboard(final View view) {
        final InputMethodManager inputMethodManager = getInputMethodManager();
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void configureRecyclerView(final RecyclerView recyclerView,
                                       final SearchPreferenceAdapter searchPreferenceAdapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(searchPreferenceAdapter);
    }

    private static void configureSearchView(final SearchView searchView,
                                            final SearchPreferenceAdapter searchPreferenceAdapter,
                                            final PreferenceSearcher preferenceSearcher,
                                            final SearchConfiguration searchConfiguration) {
        if (searchConfiguration.getTextHint() != null) {
            searchView.setQueryHint(searchConfiguration.getTextHint());
        }
        searchView.setOnQueryTextListener(
                createOnQueryTextListener(
                        searchPreferenceAdapter,
                        preferenceSearcher));
    }

    private static OnQueryTextListener createOnQueryTextListener(
            final SearchPreferenceAdapter searchPreferenceAdapter,
            final PreferenceSearcher preferenceSearcher) {
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
                searchPreferenceAdapter.setPreferenceItems(
                        ImmutableList.copyOf(
                                preferenceSearcher.searchFor(query, false)));
            }
        };
    }
}
