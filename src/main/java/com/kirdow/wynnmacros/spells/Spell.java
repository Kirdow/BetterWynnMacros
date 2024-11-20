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

    private final SpellKey[] sequence;

    Spell(boolean...sequence) {
        this.sequence = new SpellKey[sequence.length];
        for (int i = 0; i < sequence.length; i++) {
            this.sequence[i] = SpellKey.get(sequence[i]);
        }
    }

    public SpellKey[] getSequence() {
        return sequence;
    }

    public synchronized void run(Consumer<SpellKey> actuator) {
        if (engine != null) {
            Logger.dev("Aborting suggested spell");
            return;
        }

        Logger.dev("Starting %s spell", name().toLowerCase());

        Logger.dev("Requesting matching queue");
        var matchingQueue = WynnHelper.getMatchingSpellQueue(sequence);
        matchingQueue.ifPresentOrElse(rest -> {
            Logger.dev("Got remaining queue mode (len = %d)", rest.length);
            var engine = AsyncEngine.start();
            for (int i = 0; i < rest.length; i++) {
                final SpellKey cast = rest[i];
                if (i == 0)
                    engine = engine.then(() -> actuator.accept(cast));
                else
                    engine = engine.after(ConfigManager.get().baseDelay, () -> actuator.accept(cast));
            }
            engine = engine.after(ConfigManager.get().baseDelay, Spell::reset);
            Logger.dev("Started %s spell smart cast", name().toLowerCase());
            Spell.engine = engine;
        }, () -> {
            Logger.dev("Got occupied queue mode.");
            Spell.engine = AsyncEngine.start();
            WynnHelper.tryRunWait(engine -> {
                Logger.dev("Got tryRunWait engine.");
                for (int i = 0; i < sequence.length; i++) {
                    final SpellKey cast = sequence[i];
                    if (i == 0)
                        engine = engine.then(() -> actuator.accept(cast));
                    else
                        engine = engine.after(ConfigManager.get().baseDelay, () -> actuator.accept(cast));
                }
                engine = engine.after(ConfigManager.get().baseDelay, Spell::reset);
                Logger.dev("Started %s spell forced cast", name().toLowerCase());
                Spell.engine = engine;
            });
        });
    }

    private static void reset() {
        Logger.dev("Resetting cast");
        Spell.engine = null;
    }

    private static AsyncEngine engine = null;

}
