package de.KnollFrank.lib.preferencesearch.search.provider;

import androidx.preference.MultiSelectListPreference;

import de.KnollFrank.lib.preferencesearch.common.Strings;

class MultiSelectListPreferenceSearchableInfoProvider implements SearchableInfoProvider<MultiSelectListPreference> {

    @Override
    public String getSearchableInfo(final MultiSelectListPreference preference) {
        return Strings.join(preference.getEntries());
    }
}
