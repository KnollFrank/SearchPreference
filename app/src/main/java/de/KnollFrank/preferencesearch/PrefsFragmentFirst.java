package de.KnollFrank.preferencesearch;

import android.os.Bundle;

import de.KnollFrank.lib.preferencesearch.BaseSearchPreferenceFragment;

public class PrefsFragmentFirst extends BaseSearchPreferenceFragment {

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        addPreferencesFromResource(R.xml.preferences_multiple_screens);
    }
}