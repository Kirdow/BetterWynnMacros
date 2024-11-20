package com.kirdow.wynnmacros.util;

import net.minecraft.client.MinecraftClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class AsyncEngine {

    public static AsyncEngine start() {
        return new AsyncEngine(null);
    }

    private final CompletableFuture<Void> future;

    private AsyncEngine(CompletableFuture<Void> future) {
        this.future = future;
    }

    public AsyncEngine then(Runnable runnable) {
        return new AsyncEngine(run(future, null, runnable));
    }

    public AsyncEngine after(long ms, Runnable runnable) {
        return new AsyncEngine(run(future, CompletableFuture.delayedExecutor(ms, TimeUnit.MILLISECONDS), runnable));
    }

    public AsyncEngine sleep(long ms) {
        return after(ms, null);
    }

    private static void invoke(Runnable runnable) {
        MinecraftClient.getInstance().executeSync(runnable);
    }

    private static CompletableFuture<Void> run(CompletableFuture<Void> parent, Executor executor, Runnable runnable) {
        final Runnable run = () -> {
            if (runnable != null) {
                invoke(runnable);
            }
        };

        if (parent != null) {
            return executor != null ? parent.thenRunAsync(run, executor) : parent.thenRunAsync(run);
        } else {
            return executor != null ? CompletableFuture.runAsync(run, executor) : CompletableFuture.runAsync(run);
        }
    }

}
