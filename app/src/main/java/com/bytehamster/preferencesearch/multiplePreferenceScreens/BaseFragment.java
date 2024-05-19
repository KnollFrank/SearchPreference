package com.bytehamster.preferencesearch.multiplePreferenceScreens;

import static com.bytehamster.preferencesearch.multiplePreferenceScreens.MultiplePreferenceScreensExample.NAVIGATION_PATH;
import static com.bytehamster.preferencesearch.multiplePreferenceScreens.MultiplePreferenceScreensExample.KEY_OF_PREFERENCE_2_HIGHLIGHT;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.bytehamster.lib.preferencesearch.SearchPreferenceResult;

import java.util.List;
import java.util.Optional;

public abstract class BaseFragment extends PreferenceFragmentCompat {

    private List<String> navigationPath;
    private Optional<String> keyOfPreference2Highlight;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final BundleHelper bundleHelper = new BundleHelper(getArguments());
        this.navigationPath = bundleHelper.getStringArrayList(NAVIGATION_PATH);
        this.keyOfPreference2Highlight = bundleHelper.getString(KEY_OF_PREFERENCE_2_HIGHLIGHT);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.navigationPath.isEmpty()) {
            this.keyOfPreference2Highlight.ifPresent(
                    keyOfPreference2Highlight -> {
                        final SearchPreferenceResult searchPreferenceResult = new SearchPreferenceResult(keyOfPreference2Highlight, 0, null);
                        scrollToPreference(keyOfPreference2Highlight);
                        searchPreferenceResult.highlight(this);
                    });
        } else {
            ((MultiplePreferenceScreensExample) requireActivity()).navigatePathAndHighlightPreference(
                    this.navigationPath,
                    this.keyOfPreference2Highlight.get(),
                    false);
        }
    }
}
