package de.KnollFrank.lib.preferencesearch.results.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import java.text.MessageFormat;
import java.util.stream.Collectors;

import de.KnollFrank.lib.preferencesearch.PreferencePath;

class PreferencePathView {

    private static final int PREFERENCE_PATH_VIEW_ID = View.generateViewId();

    public static TextView createPreferencePathView(final Context context) {
        final TextView preferencePathView = new TextView(context);
        preferencePathView.setId(PREFERENCE_PATH_VIEW_ID);
        preferencePathView.setVisibility(View.GONE);
        return preferencePathView;
    }

    public static void displayPreferencePath(final PreferenceViewHolder holder,
                                             final PreferencePath preferencePath,
                                             final boolean showPreferencePath) {
        final TextView preferencePathView = getPreferencePathView(holder);
        if (showPreferencePath) {
            preferencePathView.setText(
                    MessageFormat.format(
                            "Path: {0}",
                            preferencePath != null ? toString(preferencePath) : ""));
            preferencePathView.setVisibility(View.VISIBLE);
        } else {
            preferencePathView.setVisibility(View.GONE);
        }
    }

    private static TextView getPreferencePathView(final PreferenceViewHolder holder) {
        return (TextView) holder.findViewById(PREFERENCE_PATH_VIEW_ID);
    }

    private static String toString(final PreferencePath preferencePath) {
        return String.join(
                " > ",
                preferencePath
                        .preferences
                        .stream()
                        .map(Preference::getTitle)
                        .collect(Collectors.toList()));
    }
}
