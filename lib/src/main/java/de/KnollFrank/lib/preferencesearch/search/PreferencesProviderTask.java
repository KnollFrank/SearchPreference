package de.KnollFrank.lib.preferencesearch.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;

import de.KnollFrank.lib.preferencesearch.PreferenceScreenWithHosts;
import de.KnollFrank.lib.preferencesearch.PreferencesProvider;

class PreferencesProviderTask extends AsyncTask<Void, Void, PreferenceScreenWithHosts> {

    private final PreferencesProvider preferencesProvider;
    private final Consumer<Runnable> runOnUiThread;
    private final Consumer<PreferenceScreenWithHosts> onPostExecute;
    private final ProgressDialog dialog;

    public PreferencesProviderTask(final PreferencesProvider preferencesProvider,
                                   final Consumer<Runnable> runOnUiThread,
                                   final Consumer<PreferenceScreenWithHosts> onPostExecute,
                                   final Context context) {
        this.preferencesProvider = preferencesProvider;
        this.runOnUiThread = runOnUiThread;
        this.onPostExecute = onPostExecute;
        this.dialog = createProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected PreferenceScreenWithHosts doInBackground(final Void... voids) {
//        try {
//            TimeUnit.SECONDS.sleep(5);
//        } catch (final InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        final FutureTask<PreferenceScreenWithHosts> task =
                new FutureTask<>(preferencesProvider::getPreferenceScreenWithHosts);
        runOnUiThread.accept(task);
        return getPreferenceScreenWithHosts(task);
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

    private static PreferenceScreenWithHosts getPreferenceScreenWithHosts(final FutureTask<PreferenceScreenWithHosts> task) {
        try {
            return task.get();
        } catch (final ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
