package de.KnollFrank.lib.preferencesearch.common;

import android.app.Activity;

public class OnUiThreadRunnerFactory {

    public static OnUiThreadRunner fromActivity(final Activity activity) {
        return new OnUiThreadRunner(activity::runOnUiThread);
    }
}
