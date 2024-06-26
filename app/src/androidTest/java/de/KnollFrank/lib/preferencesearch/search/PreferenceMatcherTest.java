package de.KnollFrank.lib.preferencesearch.search;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.test.core.app.ActivityScenario;

import org.junit.Test;

import java.util.List;

import de.KnollFrank.lib.preferencesearch.search.PreferenceMatch.Type;
import de.KnollFrank.preferencesearch.test.TestActivity;

public class PreferenceMatcherTest {

    @Test
    public void shouldGetPreferenceMatches() {
        try (final ActivityScenario<TestActivity> scenario = ActivityScenario.launch(TestActivity.class)) {
            scenario.onActivity(context -> {
                // Given
                final Preference preference = new Preference(context);
                preference.setKey("feedback");
                preference.setTitle("Title, title part");
                preference.setSummary("title in summary");

                // When
                final List<PreferenceMatch> preferenceMatches =
                        PreferenceMatcher.getPreferenceMatches(preference, "title");

                // Then
                assertThat(
                        preferenceMatches,
                        hasItems(
                                new PreferenceMatch(preference, Type.TITLE, new IndexRange(0, 5)),
                                new PreferenceMatch(preference, Type.TITLE, new IndexRange(7, 12)),
                                new PreferenceMatch(preference, Type.SUMMARY, new IndexRange(0, 5))));
            });
        }
    }

    @Test
    public void shouldGetListPreferenceMatches() {
        try (final ActivityScenario<TestActivity> scenario = ActivityScenario.launch(TestActivity.class)) {
            scenario.onActivity(context -> {
                // Given
                final String needle = "entry of";
                final ListPreference listPreference = new ListPreference(context);
                listPreference.setKey("keyOfSomeListPreference");
                listPreference.setSummary("This allows to select from a list");
                listPreference.setTitle("List preference");
                listPreference.setEntries(new String[]{"dummy entry", needle + " some ListPreference"});

                // When
                final List<PreferenceMatch> preferenceMatches =
                        PreferenceMatcher.getPreferenceMatches(listPreference, needle);

                // Then
                assertThat(
                        preferenceMatches,
                        hasItem(new PreferenceMatch(listPreference, Type.LIST_PREFERENCE_ENTRY, null)));
            });
        }
    }

    @Test
    public void shouldGetListPreferenceMatches_noEntries() {
        try (final ActivityScenario<TestActivity> scenario = ActivityScenario.launch(TestActivity.class)) {
            scenario.onActivity(context -> {
                // Given
                final String needle = "entry of";
                final ListPreference listPreference = new ListPreference(context);
                listPreference.setKey("keyOfSomeListPreference");
                listPreference.setSummary("This allows to select from a list");
                listPreference.setTitle("List preference");
                listPreference.setEntries(null);

                // When
                final List<PreferenceMatch> preferenceMatches =
                        PreferenceMatcher.getPreferenceMatches(listPreference, needle);

                // Then
                assertThat(preferenceMatches, is(empty()));
            });
        }
    }
}