package de.KnollFrank.lib.settingssearch.provider;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.KnollFrank.lib.settingssearch.ConnectedPreferenceScreens;
import de.KnollFrank.lib.settingssearch.MergedPreferenceScreen;
import de.KnollFrank.lib.settingssearch.PreferenceScreenWithHost;
import de.KnollFrank.lib.settingssearch.PreferenceScreensProvider;
import de.KnollFrank.lib.settingssearch.fragment.Fragments;
import de.KnollFrank.lib.settingssearch.search.provider.SearchableInfoAttribute;

public class MergedPreferenceScreenProvider {

    private final Fragments fragments;
    private final PreferenceScreensProvider preferenceScreensProvider;
    private final PreferenceScreensMerger preferenceScreensMerger;
    private final SearchableInfoAttribute searchableInfoAttribute;
    private final PreferenceScreenGraphAvailableListener preferenceScreenGraphAvailableListener;
    private final boolean cacheMergedPreferenceScreens;

    private static final Map<String, MergedPreferenceScreen> mergedPreferenceScreenByFragment = new HashMap<>();

    public MergedPreferenceScreenProvider(final Fragments fragments,
                                          final PreferenceScreensProvider preferenceScreensProvider,
                                          final PreferenceScreensMerger preferenceScreensMerger,
                                          final SearchableInfoAttribute searchableInfoAttribute,
                                          final PreferenceScreenGraphAvailableListener preferenceScreenGraphAvailableListener,
                                          final boolean cacheMergedPreferenceScreens) {
        this.fragments = fragments;
        this.preferenceScreensProvider = preferenceScreensProvider;
        this.preferenceScreensMerger = preferenceScreensMerger;
        this.searchableInfoAttribute = searchableInfoAttribute;
        this.preferenceScreenGraphAvailableListener = preferenceScreenGraphAvailableListener;
        this.cacheMergedPreferenceScreens = cacheMergedPreferenceScreens;
    }

    public MergedPreferenceScreen getMergedPreferenceScreen(final String rootPreferenceFragment) {
        if (!cacheMergedPreferenceScreens) {
            return computeMergedPreferenceScreen(rootPreferenceFragment);
        }
        if (!mergedPreferenceScreenByFragment.containsKey(rootPreferenceFragment)) {
            mergedPreferenceScreenByFragment.put(rootPreferenceFragment, computeMergedPreferenceScreen(rootPreferenceFragment));
        }
        return mergedPreferenceScreenByFragment.get(rootPreferenceFragment);
    }

    private MergedPreferenceScreen computeMergedPreferenceScreen(final String rootPreferenceFragment) {
        return getMergedPreferenceScreen(
                (PreferenceFragmentCompat) fragments.instantiateAndInitializeFragment(
                        rootPreferenceFragment,
                        Optional.empty()));
    }

    private MergedPreferenceScreen getMergedPreferenceScreen(final PreferenceFragmentCompat root) {
        return getMergedPreferenceScreen(getConnectedPreferenceScreens(root));
    }

    private MergedPreferenceScreen getMergedPreferenceScreen(final ConnectedPreferenceScreens screens) {
        // MUST compute A (which just reads screens) before B (which modifies screens)
        // A:
        final Map<Preference, PreferenceFragmentCompat> hostByPreference =
                HostByPreferenceProvider.getHostByPreference(screens.getConnectedPreferenceScreens());
        // B:
        final PreferenceScreensMerger.PreferenceScreenAndIsNonClickable preferenceScreenAndIsNonClickable = destructivelyMergeScreens(screens.getConnectedPreferenceScreens());
        return new MergedPreferenceScreen(
                preferenceScreenAndIsNonClickable.preferenceScreen(),
                preferenceScreenAndIsNonClickable.isNonClickable(),
                hostByPreference,
                screens.preferencePathByPreference,
                searchableInfoAttribute);
    }

    private ConnectedPreferenceScreens getConnectedPreferenceScreens(final PreferenceFragmentCompat root) {
        final ConnectedPreferenceScreens screens = preferenceScreensProvider.getConnectedPreferenceScreens(root);
        preferenceScreenGraphAvailableListener.onPreferenceScreenGraphWithoutInvisibleAndNonSearchablePreferencesAvailable(screens.preferenceScreenGraph);
        return screens;
    }

    private PreferenceScreensMerger.PreferenceScreenAndIsNonClickable destructivelyMergeScreens(final Set<PreferenceScreenWithHost> screens) {
        return preferenceScreensMerger.destructivelyMergeScreens(getPreferenceScreens(new ArrayList<>(screens)));
    }

    private static List<PreferenceScreen> getPreferenceScreens(final List<PreferenceScreenWithHost> screens) {
        return screens
                .stream()
                .map(PreferenceScreenWithHost::preferenceScreen)
                .collect(Collectors.toList());
    }
}
