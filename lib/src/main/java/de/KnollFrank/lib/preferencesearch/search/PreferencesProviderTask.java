package de.KnollFrank.lib.preferencesearch.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.function.Consumer;

import de.KnollFrank.lib.preferencesearch.PreferenceScreenWithHosts;
import de.KnollFrank.lib.preferencesearch.PreferencesProvider;
import de.KnollFrank.lib.preferencesearch.common.OnUiThreadRunner;

// FK-TODO: make this class which shows a dialog a wrapper class
class PreferencesProviderTask extends AsyncTask<Void, Void, PreferenceScreenWithHosts> {

    private final PreferencesProvider preferencesProvider;
    private final OnUiThreadRunner onUiThreadRunner;
    private final Consumer<PreferenceScreenWithHosts> onPostExecute;
    private final ProgressDialog dialog;

    public PreferencesProviderTask(final PreferencesProvider preferencesProvider,
                                   final OnUiThreadRunner onUiThreadRunner,
                                   final Consumer<PreferenceScreenWithHosts> onPostExecute,
                                   final Context context) {
        this.preferencesProvider = preferencesProvider;
        this.onUiThreadRunner = onUiThreadRunner;
        this.onPostExecute = onPostExecute;
        this.dialog = createProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected PreferenceScreenWithHosts doInBackground(final Void... voids) {
        return onUiThreadRunner.runOnUiThread(preferencesProvider::getPreferenceScreenWithHosts);
    }

    @Override
    protected void onPostExecute(final PreferenceScreenWithHosts preferenceScreenWithHosts) {
        dialog.dismiss();
        onPostExecute.accept(preferenceScreenWithHosts);
    }

    private ProgressDialog createProgressDialog(final Context context) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
