package de.KnollFrank.lib.preferencesearch.common;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.function.Consumer;

public class OnUiThreadRunner {

    private final Consumer<Runnable> runOnUiThread;

    public OnUiThreadRunner(final Consumer<Runnable> runOnUiThread) {
        this.runOnUiThread = runOnUiThread;
    }

    public <V> V runOnUiThread(final Callable<V> callable) {
        final RunnableFuture<V> task = new FutureTask<>(callable);
        runOnUiThread.accept(task);
        return get(task);
    }

    private static <V> V get(final RunnableFuture<V> task) {
        try {
            return task.get();
        } catch (final ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
