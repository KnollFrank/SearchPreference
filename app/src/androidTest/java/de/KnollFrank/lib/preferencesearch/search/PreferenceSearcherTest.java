package de.KnollFrank.lib.preferencesearch.search;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static de.KnollFrank.lib.preferencesearch.search.provider.BuiltinPreferenceDescriptionsFactory.createBuiltinPreferenceDescriptions;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import androidx.test.core.app.ActivityScenario;

import com.google.common.collect.ImmutableList;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.KnollFrank.lib.preferencesearch.MergedPreferenceScreen;
import de.KnollFrank.lib.preferencesearch.PreferenceScreenWithHostProvider;
import de.KnollFrank.lib.preferencesearch.PreferenceScreensProvider;
import de.KnollFrank.lib.preferencesearch.common.Maps;
import de.KnollFrank.lib.preferencesearch.fragment.DefaultFragmentInitializer;
import de.KnollFrank.lib.preferencesearch.fragment.Fragments;
import de.KnollFrank.lib.preferencesearch.provider.MergedPreferenceScreenProvider;
import de.KnollFrank.lib.preferencesearch.provider.PreferenceDialogProvider;
import de.KnollFrank.lib.preferencesearch.provider.PreferenceScreensMerger;
import de.KnollFrank.lib.preferencesearch.provider.SearchablePreferencePredicate;
import de.KnollFrank.lib.preferencesearch.search.provider.PreferenceDescription;
import de.KnollFrank.lib.preferencesearch.search.provider.PreferenceDescriptions;
import de.KnollFrank.lib.preferencesearch.search.provider.SearchableInfoAttribute;
import de.KnollFrank.lib.preferencesearch.search.provider.SearchableInfoByPreferenceDialogProvider;
import de.KnollFrank.lib.preferencesearch.search.provider.SearchableInfoProvider;
import de.KnollFrank.lib.preferencesearch.search.provider.SearchableInfoProviderInternal;
import de.KnollFrank.lib.preferencesearch.search.provider.SearchableInfoProviders;
import de.KnollFrank.preferencesearch.preference.custom.CustomDialogPreference;
import de.KnollFrank.preferencesearch.preference.custom.ReversedListPreference;
import de.KnollFrank.preferencesearch.preference.custom.ReversedListPreferenceSearchableInfoProvider;
import de.KnollFrank.preferencesearch.preference.fragment.CustomDialogFragment;
import de.KnollFrank.preferencesearch.preference.fragment.PrefsFragmentFirst;
import de.KnollFrank.preferencesearch.test.TestActivity;

public class PreferenceSearcherTest {

    @Test
    public void shouldSearchAndFindTitle() {
        final String keyword = "fourth";
        final String keyOfPreference = "fourthfile";
        testSearch(
                PreferenceFragment.fromSinglePreference(
                        context -> {
                            final CheckBoxPreference preference = new CheckBoxPreference(context);
                            preference.setKey(keyOfPreference);
                            preference.setTitle(String.format("Checkbox %s file", keyword));
                            return preference;
                        }),
                (preference, host) -> true,
                keyword,
                hasItem(keyOfPreference),
                (hostOfPreference, preference) -> Optional.empty(),
                preferenceDialog -> {
                    throw new IllegalStateException();
                });
    }

    @Test
    public void shouldSearchAndNotFindNonSearchablePreference() {
        final String keyword = "fourth";
        final String keyOfPreference = "fourthfile";
        testSearch(
                PreferenceFragment.fromSinglePreference(
                        context -> {
                            final CheckBoxPreference preference = new CheckBoxPreference(context);
                            preference.setKey(keyOfPreference);
                            preference.setTitle(String.format("Checkbox %s file", keyword));
                            return preference;
                        }),
                (preference, host) -> !keyOfPreference.equals(preference.getKey()),
                keyword,
                not(hasItem(keyOfPreference)),
                (hostOfPreference, preference) -> Optional.empty(),
                preferenceDialog -> {
                    throw new IllegalStateException();
                });
    }

    @Test
    public void shouldSearchAndFindSummary() {
        final String keyword = "fourth";
        final String keyOfPreference = "fourthfile";
        testSearch(
                PreferenceFragment.fromSinglePreference(
                        context -> {
                            final CheckBoxPreference preference = new CheckBoxPreference(context);
                            preference.setKey(keyOfPreference);
                            preference.setSummary(String.format("Checkbox %s file", keyword));
                            return preference;
                        }),
                (preference, host) -> true,
                keyword,
                hasItem(keyOfPreference),
                (hostOfPreference, preference) -> Optional.empty(),
                preferenceDialog -> {
                    throw new IllegalStateException();
                });
    }

    @Test
    public void shouldSearchAndFindListPreference_entries() {
        final String keyword = "entry of some ListPreference";
        final String keyOfPreference = "keyOfSomeListPreference";
        testSearch(
                PreferenceFragment.fromSinglePreference(
                        context -> {
                            final ListPreference preference = new ListPreference(context);
                            preference.setKey(keyOfPreference);
                            preference.setSummary("This allows to select from a list");
                            preference.setTitle("Select list preference");
                            preference.setEntryValues(new String[]{"some entry value"});
                            preference.setEntries(new String[]{keyword});
                            return preference;
                        }),
                (preference, host) -> true,
                keyword,
                hasItem(keyOfPreference),
                (hostOfPreference, preference) -> Optional.empty(),
                preferenceDialog -> {
                    throw new IllegalStateException();
                });
    }

    @Test
    public void shouldSearchAndFindListPreference_dialogTitle() {
        final String keyword = "this is the dialog title";
        final String keyOfPreference = "keyOfSomeListPreference";
        testSearch(
                PreferenceFragment.fromSinglePreference(
                        context -> {
                            final ListPreference preference = new ListPreference(context);
                            preference.setKey(keyOfPreference);
                            preference.setSummary("This allows to select from a list");
                            preference.setTitle("Select list preference");
                            preference.setEntries(new String[]{"some entry"});
                            preference.setEntryValues(new String[]{"some entry value"});
                            preference.setDialogTitle(keyword);
                            return preference;
                        }),
                (preference, host) -> true,
                keyword,
                hasItem(keyOfPreference),
                (hostOfPreference, preference) -> Optional.empty(),
                preferenceDialog -> {
                    throw new IllegalStateException();
                });
    }

    @Test
    public void shouldSearchAndFindSwitchPreference_summaryOff() {
        final String summaryOff = "switch is off";
        final String keyOfPreference = "keyOfSomeSwitchPreference";
        testSearch(
                PreferenceFragment.fromSinglePreference(
                        context -> {
                            final SwitchPreference preference = new SwitchPreference(context);
                            preference.setKey(keyOfPreference);
                            preference.setSummary("This allows to use a switch");
                            preference.setSummaryOff(summaryOff);
                            preference.setSummaryOn("switch is on");
                            return preference;
                        }),
                (preference, host) -> true,
                summaryOff,
                hasItem(keyOfPreference),
                (hostOfPreference, preference) -> Optional.empty(),
                preferenceDialog -> {
                    throw new IllegalStateException();
                });
    }

    @Test
    public void shouldSearchAndFindSwitchPreference_summaryOn() {
        final String summaryOn = "switch is on";
        final String keyOfPreference = "keyOfSomeSwitchPreference";
        testSearch(
                PreferenceFragment.fromSinglePreference(
                        context -> {
                            final SwitchPreference preference = new SwitchPreference(context);
                            preference.setKey(keyOfPreference);
                            preference.setSummary("This allows to use a switch");
                            preference.setSummaryOff("switch is off");
                            preference.setSummaryOn(summaryOn);
                            return preference;
                        }),
                (preference, host) -> true,
                summaryOn,
                hasItem(keyOfPreference),
                (hostOfPreference, preference) -> Optional.empty(),
                preferenceDialog -> {
                    throw new IllegalStateException();
                });
    }

    @Test
    public void shouldSearchAndFindCustomReversedListPreferenceViaReversedKeyword() {
        final String keyword = "Windows Live";
        final String keyOfPreference = "keyOfReversedListPreference";
        testSearch(
                PreferenceFragment.fromSinglePreference(
                        context -> {
                            final ReversedListPreference preference = new ReversedListPreference(context);
                            preference.setKey(keyOfPreference);
                            preference.setSummary("summary of ReversedListPreference");
                            preference.setTitle("title of ReversedListPreference");
                            preference.setEntryValues(new String[]{"some entry value"});
                            preference.setEntries(new String[]{keyword});
                            return preference;
                        }),
                (preference, host) -> true,
                ReversedListPreference.getReverse(keyword).toString(),
                hasItem(keyOfPreference),
                (hostOfPreference, preference) -> Optional.empty(),
                preferenceDialog -> {
                    throw new IllegalStateException();
                });
    }

    @Test
    public void shouldSearchAndFindInCustomDialogPreference() {
        final String keyword = "some text in a custom dialog";
        final String keyOfPreference = "keyOfCustomDialogPreference";
        testSearch(
                PreferenceFragment.fromSinglePreference(
                        context -> {
                            final CustomDialogPreference preference = new CustomDialogPreference(context);
                            preference.setKey(keyOfPreference);
                            preference.setSummary("summary of CustomDialogPreference");
                            preference.setTitle("title of CustomDialogPreference");
                            return preference;
                        }),
                (preference, host) -> true,
                keyword,
                hasItem(keyOfPreference),
                (hostOfPreference, preference) ->
                        preference instanceof CustomDialogPreference ?
                                Optional.of(new CustomDialogFragment()) :
                                Optional.empty(),
                preferenceDialog -> {
                    if (preferenceDialog instanceof final CustomDialogFragment customDialogFragment) {
                        return customDialogFragment.getSearchableInfo();
                    }
                    throw new IllegalArgumentException();
                });
    }

    @Test
    public void shouldSearchAndFindInPreferenceWithOnPreferenceClickListener() {
        final String keyword = "some text in a custom dialog";
        final String keyOfPreference = "keyOfPreferenceWithOnPreferenceClickListener";
        testSearch(
                new PrefsFragmentFirst(),
                (preference, host) -> true,
                keyword,
                hasItem(keyOfPreference),
                (hostOfPreference, preference) ->
                        keyOfPreference.equals(preference.getKey()) ?
                                Optional.of(new CustomDialogFragment()) :
                                Optional.empty(),
                preferenceDialog -> {
                    if (preferenceDialog instanceof final CustomDialogFragment customDialogFragment) {
                        return customDialogFragment.getSearchableInfo();
                    }
                    throw new IllegalArgumentException();
                });
    }

    @Test
    public void shouldSearchAndFindMultiSelectListPreference() {
        final String keyword = "entry of some MultiSelectListPreference";
        final String keyOfPreference = "keyOfSomeMultiSelectListPreference";
        testSearch(
                PreferenceFragment.fromSinglePreference(
                        context -> {
                            final MultiSelectListPreference preference = new MultiSelectListPreference(context);
                            preference.setKey(keyOfPreference);
                            preference.setSummary("This allows to select multiple entries from a list");
                            preference.setTitle("Multi select list preference");
                            preference.setEntries(new String[]{keyword});
                            preference.setEntryValues(new String[]{"some entry value"});
                            return preference;
                        }),
                (preference, host) -> true,
                keyword,
                hasItem(keyOfPreference),
                (hostOfPreference, preference) -> Optional.empty(),
                preferenceDialog -> {
                    throw new IllegalStateException();
                });
    }

    @Test
    public void shouldSearchAndFindMultiSelectListPreference_dialogTitle() {
        final String keyword = "dialog title of some MultiSelectListPreference";
        final String keyOfPreference = "keyOfSomeMultiSelectListPreference";
        testSearch(
                PreferenceFragment.fromSinglePreference(
                        context -> {
                            final MultiSelectListPreference preference = new MultiSelectListPreference(context);
                            preference.setKey(keyOfPreference);
                            preference.setSummary("This allows to select multiple entries from a list");
                            preference.setTitle("Multi select list preference");
                            preference.setEntryValues(new String[]{"some entry value"});
                            preference.setEntries(new String[]{"some entry"});
                            preference.setDialogTitle(keyword);
                            return preference;
                        }),
                (preference, host) -> true,
                keyword,
                hasItem(keyOfPreference),
                (hostOfPreference, preference) -> Optional.empty(),
                preferenceDialog -> {
                    throw new IllegalStateException();
                });
    }

    @Test
    public void shouldSearchAndNotFind() {
        final String keyword = "non_existing_keyword";
        final String keyOfPreference = "fourthfile";
        testSearch(
                PreferenceFragment.fromSinglePreference(
                        context -> {
                            final CheckBoxPreference preference = new CheckBoxPreference(context);
                            preference.setKey(keyOfPreference);
                            preference.setSummary("This checkbox is a preference coming from a fourth file");
                            preference.setTitle("Checkbox fourth file");
                            return preference;
                        }),
                (preference, host) -> true,
                keyword,
                not(hasItem(keyOfPreference)),
                (hostOfPreference, preference) -> Optional.empty(),
                preferenceDialog -> {
                    throw new IllegalStateException();
                });
    }

    private static void testSearch(final PreferenceFragmentCompat preferenceFragment,
                                   final SearchablePreferencePredicate searchablePreferencePredicate,
                                   final String keyword,
                                   final Matcher<Iterable<? super String>> preferenceKeyMatcher,
                                   final PreferenceDialogProvider preferenceDialogProvider,
                                   final SearchableInfoByPreferenceDialogProvider searchableInfoByPreferenceDialogProvider) {
        try (final ActivityScenario<TestActivity> scenario = ActivityScenario.launch(TestActivity.class)) {
            scenario.onActivity(fragmentActivity -> {
                // Given
                final MergedPreferenceScreen mergedPreferenceScreen =
                        getMergedPreferenceScreen(
                                preferenceFragment,
                                searchablePreferencePredicate,
                                fragmentActivity,
                                preferenceDialogProvider,
                                searchableInfoByPreferenceDialogProvider);
                final PreferenceSearcher preferenceSearcher =
                        new PreferenceSearcher(
                                mergedPreferenceScreen,
                                new SearchableInfoAttribute(),
                                getSearchableInfoProviderInternal(
                                        mergedPreferenceScreen,
                                        ImmutableList
                                                .<PreferenceDescription>builder()
                                                .addAll(createBuiltinPreferenceDescriptions())
                                                .add(new PreferenceDescription<>(
                                                        ReversedListPreference.class,
                                                        new ReversedListPreferenceSearchableInfoProvider()))
                                                .build()));

                // When
                final List<PreferenceMatch> preferenceMatches = preferenceSearcher.searchFor(keyword);

                // Then
                assertThat(getKeys(preferenceMatches), preferenceKeyMatcher);
            });
        }
    }

    private static MergedPreferenceScreen getMergedPreferenceScreen(
            final PreferenceFragmentCompat preferenceFragment,
            final SearchablePreferencePredicate searchablePreferencePredicate,
            final FragmentActivity fragmentActivity,
            final PreferenceDialogProvider preferenceDialogProvider,
            final SearchableInfoByPreferenceDialogProvider searchableInfoByPreferenceDialogProvider) {
        final DefaultFragmentInitializer defaultFragmentInitializer =
                new DefaultFragmentInitializer(
                        fragmentActivity.getSupportFragmentManager(),
                        TestActivity.FRAGMENT_CONTAINER_VIEW);
        final Fragments fragments =
                new Fragments(
                        (fragmentClassName, context) ->
                                fragmentClassName.equals(preferenceFragment.getClass().getName()) ?
                                        preferenceFragment :
                                        Fragment.instantiate(context, fragmentClassName),
                        defaultFragmentInitializer,
                        fragmentActivity);
        final MergedPreferenceScreenProvider mergedPreferenceScreenProvider =
                new MergedPreferenceScreenProvider(
                        fragments,
                        defaultFragmentInitializer,
                        new PreferenceScreensProvider(new PreferenceScreenWithHostProvider(fragments)),
                        new PreferenceScreensMerger(fragmentActivity),
                        searchablePreferencePredicate,
                        new SearchableInfoAttribute(),
                        preferenceDialogProvider,
                        searchableInfoByPreferenceDialogProvider,
                        false);
        return mergedPreferenceScreenProvider.getMergedPreferenceScreen(preferenceFragment.getClass().getName());
    }

    private static SearchableInfoProviderInternal getSearchableInfoProviderInternal(
            final MergedPreferenceScreen mergedPreferenceScreen,
            final List<PreferenceDescription> preferenceDescriptions) {
        return new SearchableInfoProviderInternal(
                new SearchableInfoProviders(
                        Maps.merge(
                                ImmutableList.of(
                                        PreferenceDescriptions.getSearchableInfoProvidersByPreferenceClass(preferenceDescriptions),
                                        PreferenceDescriptions.getSearchableInfoProvidersByPreferenceClass(mergedPreferenceScreen.getPreferenceDescriptions())),
                                SearchableInfoProvider::mergeWith)));
    }

    private static Set<String> getKeys(final List<PreferenceMatch> preferenceMatches) {
        return preferenceMatches
                .stream()
                .map(preferenceMatch -> preferenceMatch.preference)
                .map(Preference::getKey)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}