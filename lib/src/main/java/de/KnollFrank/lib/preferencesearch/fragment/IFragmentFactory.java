package de.KnollFrank.lib.preferencesearch.fragment;

import android.content.Context;

import androidx.fragment.app.Fragment;

public interface IFragmentFactory {

    Fragment instantiate(Context context, String fragmentClassName);
}
