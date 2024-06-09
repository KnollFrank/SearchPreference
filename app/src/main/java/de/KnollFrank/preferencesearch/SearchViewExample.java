package de.KnollFrank.preferencesearch;

import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Optional;

import de.KnollFrank.lib.preferencesearch.Navigation;
import de.KnollFrank.lib.preferencesearch.SearchConfiguration;
import de.KnollFrank.lib.preferencesearch.SearchPreferenceFragments;
import de.KnollFrank.lib.preferencesearch.SearchPreferenceResult;
import de.KnollFrank.lib.preferencesearch.SearchPreferenceResultListener;
import de.KnollFrank.lib.preferencesearch.common.UIUtils;

public class SearchViewExample extends AppCompatActivity implements SearchPreferenceResultListener {

    @IdRes
    private static final int FRAGMENT_CONTAINER_VIEW = R.id.fragmentContainerView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _setContentView(R.layout.multiple_preference_screens_example);
        if (savedInstanceState == null) {
            // FK-FIXME: when reaching SearchViewExample via the back button then changed preferences don't show their changed values (e.g. checkboxes)
            Navigation.show(
                    new PrefsFragmentFirst(),
                    false,
                    getSupportFragmentManager(),
                    FRAGMENT_CONTAINER_VIEW);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == R.id.search_action) {
            final SearchPreferenceFragments searchPreferenceFragments =
                    new SearchPreferenceFragments(
                            createSearchConfiguration(
                                    PrefsFragmentFirst.class));
            searchPreferenceFragments.showSearchPreferenceFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearchResultClicked(@NonNull final SearchPreferenceResult result) {
        Navigation.showPreferenceScreenAndHighlightPreference(
                result.getPreferenceFragmentClass().getName(),
                result.getKey(),
                this,
                FRAGMENT_CONTAINER_VIEW);
    }

    private void _setContentView(final @LayoutRes int resource) {
        final Pair<View, Integer> contentViewAndDummyFragmentContainerViewId =
                UIUtils.createContentViewAndDummyFragmentContainerViewId(
                        resource,
                        this);
        setContentView(contentViewAndDummyFragmentContainerViewId.first);
    }

    private SearchConfiguration createSearchConfiguration(final Class<? extends PreferenceFragmentCompat> rootPreferenceFragment) {
        return new SearchConfiguration(
                Optional.of(this),
                FRAGMENT_CONTAINER_VIEW,
                Optional.empty(),
                rootPreferenceFragment);
    }
}