package de.KnollFrank.lib.preferencesearch.preference;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import java.util.function.Consumer;

import de.KnollFrank.lib.preferencesearch.PreferenceWithHost;

public class SearchableSwitchPreferenceCompat extends SwitchPreferenceCompat implements IClickablePreference {

    private Consumer<PreferenceWithHost> preferenceClickListener = preferenceWithHost -> {
    };
    private Class<? extends PreferenceFragmentCompat> host;

    public SearchableSwitchPreferenceCompat(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPreferenceClickListenerAndHost(final Consumer<PreferenceWithHost> preferenceClickListener,
                                                  final Class<? extends PreferenceFragmentCompat> host) {
        this.preferenceClickListener = preferenceClickListener;
        this.host = host;
    }

    @Override
    public void performClick() {
        preferenceClickListener.accept(new PreferenceWithHost(this, host));
        super.performClick();
    }
}
