package de.KnollFrank.lib.settingssearch.db.preference;

import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.KnollFrank.lib.settingssearch.common.Preferences;

public class POJOConverter {

    public static SearchablePreferenceScreenPOJO convert2POJO(final PreferenceScreen preferenceScreen) {
        return new SearchablePreferenceScreenPOJO(convert2POJOs(getChildren(preferenceScreen)));
    }

    public static SearchablePreferencePOJO convert2POJO(final SearchablePreference searchablePreference) {
        return new SearchablePreferencePOJO(
                searchablePreference.getKey(),
                // FK-FIXME: replace 0 with real value
                0,
                searchablePreference.getLayoutResource(),
                // FK-TODO: DRY the following two arguments of SearchablePreferencePOJO
                Optional
                        .ofNullable(searchablePreference.getSummary())
                        .map(CharSequence::toString)
                        .orElse(null),
                Optional
                        .ofNullable(searchablePreference.getTitle())
                        .map(CharSequence::toString)
                        .orElse(null),
                searchablePreference.getWidgetLayoutResource(),
                searchablePreference.getFragment(),
                searchablePreference.isVisible(),
                searchablePreference.getSearchableInfo().orElse(null),
                convert2POJOs(getChildren(searchablePreference)));
    }

    private static List<SearchablePreferencePOJO> convert2POJOs(final List<SearchablePreference> searchablePreferences) {
        return searchablePreferences
                .stream()
                .map(POJOConverter::convert2POJO)
                .collect(Collectors.toList());
    }

    private static List<SearchablePreference> getChildren(final PreferenceGroup preferenceGroup) {
        return Preferences
                .getDirectChildren(preferenceGroup)
                .stream()
                .map(SearchablePreference.class::cast)
                .collect(Collectors.toList());
    }
}
