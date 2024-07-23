package de.KnollFrank.lib.preferencesearch.fragment;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class FragmentInitializer implements PreferenceDialogs {

    private final FragmentManager fragmentManager;
    private final @IdRes int containerViewId;

    public FragmentInitializer(final FragmentManager fragmentManager, final @IdRes int containerViewId) {
        this.fragmentManager = fragmentManager;
        this.containerViewId = containerViewId;
    }

    public void initialize(final Fragment fragment) {
        this
                .fragmentManager
                .beginTransaction()
                .replace(this.containerViewId, fragment)
                .commitNow();
    }

    @Override
    public void showPreferenceDialog(final Fragment preferenceDialog) {
        this
                .fragmentManager
                .beginTransaction()
                .add(this.containerViewId, preferenceDialog)
                .commitNow();
    }

    @Override
    public void hidePreferenceDialog(final Fragment preferenceDialog) {
        this
                .fragmentManager
                .beginTransaction()
                .remove(preferenceDialog)
                .commitNow();
    }
}
