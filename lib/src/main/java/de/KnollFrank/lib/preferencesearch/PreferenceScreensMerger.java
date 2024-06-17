package de.KnollFrank.lib.preferencesearch;

import android.content.Context;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

import java.util.List;

import de.KnollFrank.lib.preferencesearch.common.Preferences;

class PreferenceScreensMerger {

    private final Context context;

    public PreferenceScreensMerger(final Context context) {
        this.context = context;
    }

    public PreferenceScreen destructivelyMergeScreens(final List<PreferenceScreen> screens) {
        final PreferenceScreen mergedScreens = screens.get(0).getPreferenceManager().createPreferenceScreen(context);
        for (final PreferenceScreen screen : screens) {
            destructivelyMergeSrcIntoDst(screen, mergedScreens);
        }
        return mergedScreens;
    }

    private void destructivelyMergeSrcIntoDst(final PreferenceScreen src, final PreferenceScreen dst) {
        final PreferenceCategory screenCategory = createScreenCategory(src);
        dst.addPreference(screenCategory);
        moveChildrenOfSrc2Dst(src, screenCategory);
    }

    private PreferenceCategory createScreenCategory(final PreferenceScreen screen) {
        final PreferenceCategory screenCategory = new PreferenceCategory(context);
        screenCategory.setTitle(screen.toString());
        return screenCategory;
    }

    private static void moveChildrenOfSrc2Dst(final PreferenceGroup src, final PreferenceGroup dst) {
        for (final Preference child : Preferences.getDirectChildren(src)) {
            addPreference2PreferenceGroup(child, dst);
            // FK-TODO: weglassen?
            if (child instanceof PreferenceGroup) {
                moveChildrenOfSrc2Dst((PreferenceGroup) child, (PreferenceGroup) child);
            }
        }
    }

    private static void addPreference2PreferenceGroup(final Preference preference,
                                                      final PreferenceGroup preferenceGroup) {
        removePreferenceFromItsParent(preference);
        preferenceGroup.addPreference(preference);
    }

    private static void removePreferenceFromItsParent(final Preference preference) {
        final PreferenceGroup parent = preference.getParent();
        if (parent != null) {
            parent.removePreference(preference);
        }
    }
}
