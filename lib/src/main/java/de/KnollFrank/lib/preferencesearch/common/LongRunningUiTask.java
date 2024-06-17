package de.KnollFrank.lib.preferencesearch.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

// FK-TODO: make this class which shows a dialog a wrapper class
public class LongRunningUiTask<V> extends AsyncTask<Void, Void, V> {

    private final Callable<V> calculateUiResult;
    private final Consumer<V> doWithUiResult;
    private final OnUiThreadRunner onUiThreadRunner;
    private final ProgressDialog dialog;

    public LongRunningUiTask(final Callable<V> calculateUiResult,
                             final Consumer<V> doWithUiResult,
                             final OnUiThreadRunner onUiThreadRunner,
                             final Context context) {
        this.calculateUiResult = calculateUiResult;
        this.doWithUiResult = doWithUiResult;
        this.onUiThreadRunner = onUiThreadRunner;
        this.dialog = createProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected V doInBackground(final Void... voids) {
        return onUiThreadRunner.runOnUiThread(calculateUiResult);
    }

    @Override
    protected void onPostExecute(final V result) {
        dialog.dismiss();
        doWithUiResult.accept(result);
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
