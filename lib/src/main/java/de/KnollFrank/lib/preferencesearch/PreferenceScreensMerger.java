package de.KnollFrank.lib.preferencesearch;

import static de.KnollFrank.lib.preferencesearch.common.Preferences.getDirectChildren;

import android.content.Context;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

import java.util.List;

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
        movePreferencesOfScreen2Category(src, screenCategory);
    }

    private PreferenceCategory createScreenCategory(final PreferenceScreen screen) {
        final PreferenceCategory screenCategory = new PreferenceCategory(context);
        screenCategory.setTitle(screen.toString());
        return screenCategory;
    }

    private static void movePreferencesOfScreen2Category(final PreferenceScreen screen,
                                                         final PreferenceCategory category) {
        for (final Preference preference : getDirectChildren(screen)) {
            preference.setEnabled(false);
            preference.setShouldDisableView(false);
            removePreferenceFromItsParent(preference);
            category.addPreference(preference);
        }
    }

    private static void removePreferenceFromItsParent(final Preference preference) {
        final PreferenceGroup parent = preference.getParent();
        if (parent != null) {
            parent.removePreference(preference);
        }
    }
}
