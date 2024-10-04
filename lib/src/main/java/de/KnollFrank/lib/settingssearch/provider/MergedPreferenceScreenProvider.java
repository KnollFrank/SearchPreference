package de.KnollFrank.lib.settingssearch.provider;

import android.content.Context;

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

import de.KnollFrank.lib.settingssearch.ConnectedSearchablePreferenceScreens;
import de.KnollFrank.lib.settingssearch.MergedPreferenceScreen;
import de.KnollFrank.lib.settingssearch.PreferenceScreenWithHostClass;
import de.KnollFrank.lib.settingssearch.fragment.FragmentFactoryAndInitializer;
import de.KnollFrank.lib.settingssearch.fragment.PreferencePathNavigator;
import de.KnollFrank.lib.settingssearch.graph.PreferenceScreensProvider;
import de.KnollFrank.lib.settingssearch.fragment.Fragments;
import de.KnollFrank.lib.settingssearch.search.provider.SearchableInfoAttribute;

public class MergedPreferenceScreenProvider {

    private final Fragments fragments;
    private final PreferenceScreensProvider preferenceScreensProvider;
    private final PreferenceScreensMerger preferenceScreensMerger;
    private final SearchableInfoAttribute searchableInfoAttribute;
    private final boolean cacheMergedPreferenceScreens;
    private final FragmentFactoryAndInitializer fragmentFactoryAndInitializer;
    private final Context context;

    private static final Map<String, MergedPreferenceScreen> mergedPreferenceScreenByFragment = new HashMap<>();

    public MergedPreferenceScreenProvider(final Fragments fragments,
                                          final PreferenceScreensProvider preferenceScreensProvider,
                                          final PreferenceScreensMerger preferenceScreensMerger,
                                          final SearchableInfoAttribute searchableInfoAttribute,
                                          final boolean cacheMergedPreferenceScreens,
                                          final FragmentFactoryAndInitializer fragmentFactoryAndInitializer,
                                          final Context context) {
        this.fragments = fragments;
        this.preferenceScreensProvider = preferenceScreensProvider;
        this.preferenceScreensMerger = preferenceScreensMerger;
        this.searchableInfoAttribute = searchableInfoAttribute;
        this.cacheMergedPreferenceScreens = cacheMergedPreferenceScreens;
        this.fragmentFactoryAndInitializer = fragmentFactoryAndInitializer;
        this.context = context;
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
        return getMergedPreferenceScreen(preferenceScreensProvider.getConnectedPreferenceScreens(root));
    }

    private MergedPreferenceScreen getMergedPreferenceScreen(final ConnectedSearchablePreferenceScreens screens) {
        // MUST compute A (which just reads screens) before B (which modifies screens)
        // A:
        final Map<Preference, Class<? extends PreferenceFragmentCompat>> hostByPreference =
                HostByPreferenceProvider.getHostByPreference(screens.connectedSearchablePreferenceScreens());
        // B:
        final PreferenceScreensMerger.PreferenceScreenAndIsNonClickable preferenceScreenAndIsNonClickable = destructivelyMergeScreens(screens.connectedSearchablePreferenceScreens());
        return new MergedPreferenceScreen(
                preferenceScreenAndIsNonClickable.preferenceScreen(),
                preferenceScreenAndIsNonClickable.isNonClickable(),
                screens.preferencePathByPreference(),
                searchableInfoAttribute,
                new PreferencePathNavigator(hostByPreference, fragmentFactoryAndInitializer, context));
    }

    private PreferenceScreensMerger.PreferenceScreenAndIsNonClickable destructivelyMergeScreens(final Set<PreferenceScreenWithHostClass> screens) {
        return preferenceScreensMerger.destructivelyMergeScreens(getPreferenceScreens(new ArrayList<>(screens)));
    }

    private static List<PreferenceScreen> getPreferenceScreens(final List<PreferenceScreenWithHostClass> screens) {
        return screens
                .stream()
                .map(PreferenceScreenWithHostClass::preferenceScreen)
                .collect(Collectors.toList());
    }
}
