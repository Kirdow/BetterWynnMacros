package com.kirdow.wynnmacros.input;

import net.minecraft.client.option.KeyBinding;

import java.util.HashSet;
import java.util.Set;

public class KeyLock {

    public final KeyBinding bind;

    private boolean lastState = false;
    private boolean press = false;

    public KeyLock(KeyBinding bind) {
        this.bind = bind;
        LOCKS.add(this);
    }

    private void tick() {
        boolean state = bind.isPressed();

        press = state && !lastState;

        lastState = state;
    }

    public boolean isPress() {
        return press;
    }

    public static void tickAll() {
        LOCKS.forEach(KeyLock::tick);
    }

    private static final Set<KeyLock> LOCKS = new HashSet<>();
}
