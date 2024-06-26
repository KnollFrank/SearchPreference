package de.KnollFrank.lib.preferencesearch.search;

import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import de.KnollFrank.lib.preferencesearch.search.PreferenceMatch.Type;

class ListPreferenceEntryMatcher {

    // FK-TODO: introduce EntriesProvider interface and wrap ListPreference and MultiSelectListPreference
    public static List<PreferenceMatch> getEntryMatches(final ListPreference haystack, final String needle) {
        return matchesAnyEntry(haystack.getEntries(), needle) ?
                ImmutableList.of(new PreferenceMatch(haystack, Type.ENTRY, null)) :
                Collections.emptyList();
    }

    public static List<PreferenceMatch> getEntryMatches(final MultiSelectListPreference haystack, final String needle) {
        return matchesAnyEntry(haystack.getEntries(), needle) ?
                ImmutableList.of(new PreferenceMatch(haystack, Type.ENTRY, null)) :
                Collections.emptyList();
    }

    private static boolean matchesAnyEntry(final CharSequence[] entries, final String needle) {
        if(entries == null) {
            return false;
        }
        return Arrays
                .stream(entries)
                .map(CharSequence::toString)
                .anyMatch(entry -> hasAnyPreferenceMatch(entry, needle));
    }

    private static boolean hasAnyPreferenceMatch(final String haystack, final String needle) {
        final List<PreferenceMatch> preferenceMatches =
                PreferenceMatcher.getPreferenceMatches(Optional.of(haystack), needle, indexRange -> null);
        return !preferenceMatches.isEmpty();
    }
}
