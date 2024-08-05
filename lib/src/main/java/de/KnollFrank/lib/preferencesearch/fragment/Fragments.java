package de.KnollFrank.lib.preferencesearch.fragment;

import android.content.Context;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;

import java.util.Optional;
import java.util.function.Consumer;

public class Fragments {

    private final FragmentFactoryAndInitializer fragmentFactoryAndInitializer;
    private final Context context;

    public Fragments(final FragmentFactoryAndInitializer fragmentFactoryAndInitializer,
                     final Context context) {
        this.fragmentFactoryAndInitializer =
                new FragmentFactoryAndInitializerWithCache(fragmentFactoryAndInitializer);
        this.context = context;
    }

    public Fragment instantiateAndInitializeFragment(final String fragmentClassName,
                                                     final Optional<Preference> src) {
        return fragmentFactoryAndInitializer.instantiateAndInitializeFragment(fragmentClassName, src, context);
    }

    public static <T extends Fragment> void showFragment(final T fragment,
                                                         final Consumer<T> onFragmentShown,
                                                         final boolean addToBackStack,
                                                         final @IdRes int containerViewId,
                                                         final FragmentManager fragmentManager) {
        final FragmentTransaction fragmentTransaction =
                fragmentManager
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(containerViewId, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        executeOnceOnFragmentStarted(fragment, onFragmentShown, fragmentManager);
        fragmentTransaction.commit();
    }

    private static <T extends Fragment> void executeOnceOnFragmentStarted(
            final T fragment,
            final Consumer<T> onFragmentStarted,
            final FragmentManager fragmentManager) {
        fragmentManager.registerFragmentLifecycleCallbacks(
                new FragmentLifecycleCallbacks() {

                    @Override
                    public void onFragmentStarted(@NonNull final FragmentManager fragmentManager,
                                                  @NonNull final Fragment _fragment) {
                        if (_fragment == fragment) {
                            fragmentManager.unregisterFragmentLifecycleCallbacks(this);
                            onFragmentStarted.accept(fragment);
                        }
                    }
                },
                false);
    }
}
