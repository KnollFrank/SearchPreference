package de.KnollFrank.lib.preferencesearch.results;

import static de.KnollFrank.lib.preferencesearch.BaseSearchPreferenceFragment.KEY_OF_PREFERENCE_2_HIGHLIGHT;
import static de.KnollFrank.lib.preferencesearch.results.PreferenceScreenForSearchPreparer.preparePreferenceScreenForSearch;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import de.KnollFrank.lib.preferencesearch.MergedPreferenceScreen;
import de.KnollFrank.lib.preferencesearch.fragment.navigation.Commit;
import de.KnollFrank.lib.preferencesearch.fragment.navigation.Navigation;

// FK-TODO: die PreferenceCategory im Suchergebnis, die den Namen eines PreferenceScreens anzeigt, soll nicht anklickbar sein.
public class SearchResultsPreferenceFragment extends PreferenceFragmentCompat {

    private MergedPreferenceScreen mergedPreferenceScreen;
    private @IdRes int fragmentContainerViewId;

    public static SearchResultsPreferenceFragment newInstance(final @IdRes int fragmentContainerViewId,
                                                              final MergedPreferenceScreen mergedPreferenceScreen) {
        final SearchResultsPreferenceFragment searchResultsPreferenceFragment = Factory.newInstance(fragmentContainerViewId);
        searchResultsPreferenceFragment.setMergedPreferenceScreen(mergedPreferenceScreen);
        return searchResultsPreferenceFragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        new Factory().setInstanceVariables();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(@Nullable final Bundle savedInstanceState, @Nullable final String rootKey) {
        setPreferenceScreen(this.mergedPreferenceScreen.preferenceScreen);
    }

    @NonNull
    @Override
    protected Adapter onCreateAdapter(@NonNull final PreferenceScreen preferenceScreen) {
        // FK-TODO: die Preferences des preferenceScreen sollen ihren aktuellen Zustand widerspiegeln (z.B. soll der Haken einer CheckBoxPreference gemäß den darunterliegenden Daten gesetzt oder nicht gesetzt sein.)
        return new ClickablePreferenceGroupAdapter(
                preferenceScreen,
                this::showPreferenceScreenAndHighlightPreference);
    }

    private void setMergedPreferenceScreen(final MergedPreferenceScreen mergedPreferenceScreen) {
        preparePreferenceScreenForSearch(mergedPreferenceScreen.preferenceScreen);
        this.mergedPreferenceScreen = mergedPreferenceScreen;
    }

    private void showPreferenceScreenAndHighlightPreference(final Preference preference) {
        if (preference instanceof PreferenceGroup) {
            return;
        }
        this
                .mergedPreferenceScreen
                .findHostByPreference(preference)
                .ifPresent(host -> showPreferenceScreenAndHighlightPreference(host, preference));
    }

    private void showPreferenceScreenAndHighlightPreference(
            final Class<? extends PreferenceFragmentCompat> fragmentOfPreferenceScreen,
            final Preference preference2Highlight) {
        Navigation.show(
                Fragment.instantiate(
                        getActivity(),
                        fragmentOfPreferenceScreen.getName(),
                        createArguments(preference2Highlight.getKey())),
                true,
                getActivity().getSupportFragmentManager(),
                this.fragmentContainerViewId,
                Commit.COMMIT_ASYNC);
    }

    private static Bundle createArguments(final String keyOfPreference2Highlight) {
        final Bundle arguments = new Bundle();
        arguments.putString(KEY_OF_PREFERENCE_2_HIGHLIGHT, keyOfPreference2Highlight);
        return arguments;
    }

    private class Factory {

        private static final String FRAGMENT_CONTAINER_VIEW_ID = "fragmentContainerViewId";

        public static SearchResultsPreferenceFragment newInstance(final @IdRes int fragmentContainerViewId) {
            final SearchResultsPreferenceFragment fragment = new SearchResultsPreferenceFragment();
            fragment.setArguments(createArguments(fragmentContainerViewId));
            return fragment;
        }

        public void setInstanceVariables() {
            SearchResultsPreferenceFragment.this.fragmentContainerViewId =
                    requireArguments().getInt(FRAGMENT_CONTAINER_VIEW_ID);
        }

        private static Bundle createArguments(final @IdRes int fragmentContainerViewId) {
            final Bundle bundle = new Bundle();
            bundle.putInt(FRAGMENT_CONTAINER_VIEW_ID, fragmentContainerViewId);
            return bundle;
        }
    }
}
