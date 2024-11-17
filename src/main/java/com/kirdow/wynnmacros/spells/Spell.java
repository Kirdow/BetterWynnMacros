package com.kirdow.wynnmacros.spells;

import com.kirdow.wynnmacros.Logger;
import com.kirdow.wynnmacros.config.ConfigManager;
import com.kirdow.wynnmacros.util.AsyncEngine;
import com.kirdow.wynnmacros.util.WynnHelper;

import java.util.function.Consumer;

public enum Spell {

    FIRST(true, false, true),
    SECOND(true, true, true),
    THIRD(true, false, false),
    FOURTH(true, true, false);

    private final boolean[] sequence;

    Spell(boolean...sequence) {
        this.sequence = new boolean[sequence.length];
        for (int i = 0; i < sequence.length; i++) {
            this.sequence[i] = sequence[i];
        }
    }

    public boolean[] getSequence() {
        return sequence;
    }

    public synchronized void run(Consumer<Boolean> actuator) {
        if (engine != null) {
            Logger.debug("Aborting suggested spell");
            return;
        }

        Logger.debug("Starting %s spell", name().toLowerCase());

        Logger.debug("Requesting matching queue");
        var matchingQueue = WynnHelper.getMatchingSpellQueue(sequence);
        matchingQueue.ifPresentOrElse(rest -> {
            Logger.debug("Got remaining queue mode (len = %d)", rest.length);
            var engine = AsyncEngine.start();
            for (int i = 0; i < rest.length; i++) {
                final boolean cast = rest[i];
                if (i == 0)
                    engine = engine.then(() -> actuator.accept(cast));
                else
                    engine = engine.after(ConfigManager.get().baseDelay, () -> actuator.accept(cast));
            }
            engine = engine.after(ConfigManager.get().baseDelay, Spell::reset);
            Logger.debug("Started %s spell smart cast", name().toLowerCase());
            Spell.engine = engine;
        }, () -> {
            Logger.debug("Got occupied queue mode.");
            Spell.engine = AsyncEngine.start();
            WynnHelper.tryRunWait(engine -> {
                Logger.debug("Got tryRunWait engine.");
                for (int i = 0; i < sequence.length; i++) {
                    final boolean cast = sequence[i];
                    if (i == 0)
                        engine = engine.then(() -> actuator.accept(cast));
                    else
                        engine = engine.after(ConfigManager.get().baseDelay, () -> actuator.accept(cast));
                }
                engine = engine.after(ConfigManager.get().baseDelay, Spell::reset);
                Logger.debug("Started %s spell forced cast", name().toLowerCase());
                Spell.engine = engine;
            });
        });
    }

    private static void reset() {
        Logger.debug("Resetting cast");
        Spell.engine = null;
    }

    private static AsyncEngine engine = null;

}
