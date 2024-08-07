package de.KnollFrank.lib.preferencesearch.fragment;

import android.content.Context;

import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentManager;

public class FragmentsFactory {

    public static Fragments createFragments(final FragmentFactory fragmentFactory,
                                            final Context context,
                                            final FragmentManager fragmentManager,
                                            final @IdRes int containerViewId) {
        return new Fragments(
                fragmentFactory,
                new DefaultFragmentInitializer(fragmentManager, containerViewId),
                context);
    }
}
