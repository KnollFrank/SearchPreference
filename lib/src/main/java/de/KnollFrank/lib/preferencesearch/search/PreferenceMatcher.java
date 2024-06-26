package de.KnollFrank.lib.preferencesearch.search;

import static de.KnollFrank.lib.preferencesearch.search.PreferenceAttributes.getSummaryAsString;
import static de.KnollFrank.lib.preferencesearch.search.PreferenceAttributes.getTitleAsString;

import android.text.TextUtils;

import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.KnollFrank.lib.preferencesearch.common.Lists;
import de.KnollFrank.lib.preferencesearch.common.Strings;
import de.KnollFrank.lib.preferencesearch.search.PreferenceMatch.Type;

class PreferenceMatcher {

    public static List<PreferenceMatch> getPreferenceMatches(final Preference haystack,
                                                             final String needle) {
        if (TextUtils.isEmpty(needle)) {
            return Collections.emptyList();
        }
        return Lists.concat(
                getTitlePreferenceMatches(haystack, needle),
                getSummaryPreferenceMatch(haystack, needle),
                getListPreferenceEntryMatches(haystack, needle),
                getMultiSelectListPreferenceEntryMatches(haystack, needle));
    }

    private static List<PreferenceMatch> getTitlePreferenceMatches(final Preference haystack,
                                                                   final String needle) {
        return getPreferenceMatches(
                getTitleAsString(haystack),
                needle,
                indexRange -> new PreferenceMatch(haystack, Type.TITLE, indexRange));
    }

    private static List<PreferenceMatch> getSummaryPreferenceMatch(final Preference haystack,
                                                                   final String needle) {
        return getPreferenceMatches(
                getSummaryAsString(haystack),
                needle,
                indexRange -> new PreferenceMatch(haystack, Type.SUMMARY, indexRange));
    }

    private static List<PreferenceMatch> getListPreferenceEntryMatches(final Preference haystack,
                                                                       final String needle) {
        if (!(haystack instanceof ListPreference)) {
            return Collections.emptyList();
        }
        return ListPreferenceEntryMatcher.getEntryMatches((ListPreference) haystack, needle);
    }

    private static List<PreferenceMatch> getMultiSelectListPreferenceEntryMatches(final Preference haystack,
                                                                                  final String needle) {
        if (!(haystack instanceof MultiSelectListPreference)) {
            return Collections.emptyList();
        }
        return ListPreferenceEntryMatcher.getEntryMatches((MultiSelectListPreference) haystack, needle);
    }

    static List<PreferenceMatch> getPreferenceMatches(
            final Optional<String> haystack,
            final String needle,
            final Function<IndexRange, PreferenceMatch> createPreferenceMatch) {
        return haystack
                .map(_haystack -> getPreferenceMatches(_haystack, needle, createPreferenceMatch))
                .orElse(Collections.emptyList());
    }

    private static List<PreferenceMatch> getPreferenceMatches(
            final String haystack,
            final String needle,
            final Function<IndexRange, PreferenceMatch> createPreferenceMatch) {
        return Strings
                .getIndicesOfNeedleWithinHaystack(haystack.toLowerCase(), needle.toLowerCase())
                .stream()
                .map(index -> createPreferenceMatch.apply(new IndexRange(index, index + needle.length())))
                .collect(Collectors.toList());
    }
}
