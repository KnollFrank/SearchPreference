package de.KnollFrank.lib.preferencesearch.search;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;

import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.test.core.app.ActivityScenario;

import org.hamcrest.Matcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.KnollFrank.lib.preferencesearch.FragmentsFactory;
import de.KnollFrank.lib.preferencesearch.MergedPreferenceScreen;
import de.KnollFrank.lib.preferencesearch.PreferenceScreenWithHostProvider;
import de.KnollFrank.lib.preferencesearch.PreferenceScreensProvider;
import de.KnollFrank.lib.preferencesearch.common.Preferences;
import de.KnollFrank.lib.preferencesearch.fragment.Fragments;
import de.KnollFrank.lib.preferencesearch.provider.MergedPreferenceScreenProvider;
import de.KnollFrank.lib.preferencesearch.provider.PreferenceScreensMerger;
import de.KnollFrank.preferencesearch.test.TestActivity;

public class PreferenceSearcherTest {

    @BeforeClass
    public static void beforeClass() {
        Looper.prepare();
    }

    @AfterClass
    public static void afterClass() {
        Looper.getMainLooper().quitSafely();
    }

    @Test
    public void shouldSearchAndFind() {
        final String keyword = "fourth";
        testSearch(PrefsFragment.class, keyword, hasItem(containsString(keyword)));
    }

    @Test
    public void shouldSearchAndNotFind() {
        final String keyword = "non_existing_keyword";
        testSearch(PrefsFragment.class, keyword, not(hasItem(containsString(keyword))));
    }

    public static class PrefsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
            final Context context = getPreferenceManager().getContext();
            final PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

            final CheckBoxPreference checkBoxPreference = new CheckBoxPreference(context);
            checkBoxPreference.setKey("fourthfile");
            checkBoxPreference.setSummary("This checkbox is a preference coming from a fourth file");
            checkBoxPreference.setTitle("Checkbox fourth file");

            screen.addPreference(checkBoxPreference);
            setPreferenceScreen(screen);
        }
    }

    private static void testSearch(final Class<? extends PreferenceFragmentCompat> preferenceScreen,
                                   final String keyword,
                                   final Matcher<Iterable<? super String>> titlesMatcher) {
        try (final ActivityScenario<TestActivity> scenario = ActivityScenario.launch(TestActivity.class)) {
            scenario.onActivity(fragmentActivity -> {
                // Given
                final PreferenceSearcher preferenceSearcher =
                        new PreferenceSearcher(
                                getPreferences(preferenceScreen, fragmentActivity));

                // When
                final List<PreferenceMatch> preferenceMatches = preferenceSearcher.searchFor(keyword);

                // Then
                assertThat(getTitles(preferenceMatches), titlesMatcher);
            });
        }
    }

    private static List<Preference> getPreferences(final Class<? extends PreferenceFragmentCompat> preferenceScreen, final TestActivity fragmentActivity) {
        final MergedPreferenceScreen mergedPreferenceScreen = getMergedPreferenceScreen(preferenceScreen, fragmentActivity);
        return Preferences.getAllPreferences(mergedPreferenceScreen.preferenceScreen);
    }

    private static MergedPreferenceScreen getMergedPreferenceScreen(final Class<? extends PreferenceFragmentCompat> preferenceScreen, final TestActivity fragmentActivity) {
        final Fragments fragments = FragmentsFactory.createFragments(fragmentActivity);
        final MergedPreferenceScreenProvider mergedPreferenceScreenProvider =
                new MergedPreferenceScreenProvider(
                        preferenceScreen.getName(),
                        fragments,
                        new PreferenceScreensProvider(new PreferenceScreenWithHostProvider(fragments)),
                        new PreferenceScreensMerger(fragmentActivity));
        return mergedPreferenceScreenProvider.getMergedPreferenceScreen();
    }

    private static List<String> getTitles(final List<PreferenceMatch> preferenceMatches) {
        return preferenceMatches
                .stream()
                .map(preferenceMatch -> preferenceMatch.preference)
                .map(Preference::getTitle)
                .filter(Objects::nonNull)
                .map(CharSequence::toString)
                .collect(Collectors.toList());
    }
}