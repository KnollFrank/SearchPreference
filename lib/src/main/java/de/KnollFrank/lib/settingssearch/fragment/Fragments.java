package de.KnollFrank.lib.settingssearch.fragment;

import android.content.Context;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks;
import androidx.fragment.app.FragmentTransaction;

import java.util.Optional;
import java.util.function.Consumer;

import de.KnollFrank.lib.settingssearch.PreferenceWithHost;
import de.KnollFrank.lib.settingssearch.fragment.factory.FragmentFactoryAndInitializerWithCache;

public class Fragments {

    private final FragmentFactoryAndInitializerWithCache fragmentFactoryAndInitializer;
    private final Context context;

    public Fragments(final FragmentFactoryAndInitializerWithCache fragmentFactoryAndInitializer,
                     final Context context) {
        this.fragmentFactoryAndInitializer = fragmentFactoryAndInitializer;
        this.context = context;
    }

    public Fragment instantiateAndInitializeFragment(final String fragmentClassName,
                                                     final Optional<PreferenceWithHost> src) {
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
        executeOnceOnFragmentResumed(fragment, onFragmentShown, fragmentManager);
        fragmentTransaction.commit();
    }

    private static <T extends Fragment> void executeOnceOnFragmentResumed(
            final T fragment,
            final Consumer<T> onFragmentResumed,
            final FragmentManager fragmentManager) {
        fragmentManager.registerFragmentLifecycleCallbacks(
                new FragmentLifecycleCallbacks() {

                    @Override
                    public void onFragmentResumed(@NonNull final FragmentManager fragmentManager,
                                                  @NonNull final Fragment _fragment) {
                        if (_fragment == fragment) {
                            fragmentManager.unregisterFragmentLifecycleCallbacks(this);
                            onFragmentResumed.accept(fragment);
                        }
                    }
                },
                false);
    }
}
