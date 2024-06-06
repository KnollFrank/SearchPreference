package com.bytehamster.lib.preferencesearch.common;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.FragmentContainerView;

public class UIUtils {

    public static Pair<View, Integer> createContentViewAndDummyFragmentContainerViewId(
            final @LayoutRes int resource,
            final Activity activity) {
        final ViewGroup contentView = (ViewGroup) activity.getLayoutInflater().inflate(resource, null);
        final FragmentContainerView fragmentContainerView = addFragmentContainerView2ViewGroup(contentView, activity);
        return Pair.create(contentView, fragmentContainerView.getId());
    }

    public static FragmentContainerView addFragmentContainerView2ViewGroup(final ViewGroup viewGroup,
                                                                           final Activity activity) {
        final FragmentContainerView fragmentContainerView = createGoneFragmentContainerView(activity);
        viewGroup.addView(fragmentContainerView);
        return fragmentContainerView;
    }

    private static FragmentContainerView createGoneFragmentContainerView(final Context context) {
        final FragmentContainerView fragmentContainerView = new FragmentContainerView(context);
        fragmentContainerView.setId(View.generateViewId());
        fragmentContainerView.setVisibility(View.GONE);
        return fragmentContainerView;
    }
}
