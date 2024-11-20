package com.kirdow.wynnmacros.spells;

import com.kirdow.wynnmacros.Logger;
import com.kirdow.wynnmacros.input.KeyBindings.SpellBinding;
import com.kirdow.wynnmacros.util.WynnHelper;
import net.minecraft.client.MinecraftClient;

public class SpellEngine {

    public static SpellEngine get() {
        if (I != null) return I;
        return I = new SpellEngine();
    }

    private static SpellEngine I;

    private SpellEngine() {
    }

    public void useSpell(SpellBinding bind) {
        Spell[] spells = Spell.values();
        if (bind.ordinal() < spells.length) {
            spells[bind.ordinal()].run(this::actuator);
        }
    }

    private void actuator(SpellKey key) {
        WynnHelper.sendPacket(key.mask(WynnHelper.isBow()).createPacket());
    }

    public static void post(SpellBinding bind) {
        get().useSpell(bind);
    }

    private static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

}
