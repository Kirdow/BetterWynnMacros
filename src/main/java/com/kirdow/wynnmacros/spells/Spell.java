package com.kirdow.wynnmacros.spells;

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

    public void run(Consumer<Boolean> actuator) {
        if (engine != null) {
            return;
        }

        var engine = WynnHelper.checkSpellSequence();
        for (int i = 0; i < sequence.length; i++) {
            final boolean cast = sequence[i];
            if (i == 0)
                engine.then(() -> actuator.accept(cast));
            else
                engine.after(100L, () -> actuator.accept(cast));
        }
        engine.after(100L, () -> Spell.engine = null);
        Spell.engine = engine;
    }

    private static AsyncEngine engine = null;

}
