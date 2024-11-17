package com.kirdow.wynnmacros.input;

import net.minecraft.client.option.KeyBinding;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import static com.kirdow.wynnmacros.WynnMacros.wildcard;

public class KeyBindings {

    private static SpellBinding[] spellBinds;
    public static KeyBinding[] keySpells;
    public static final char[] keyDefaults = new char[] {'R', 'F', 'V', 'Q' };

    public static void init() {
        keySpells = new KeyBinding[keyDefaults.length];
        spellBinds = new SpellBinding[keyDefaults.length];
        for (int i = 0; i < keyDefaults.length; i++) {
            KeyBinding bind = keySpells[i] = new KeyBinding(wildcard("key.<id>.spell." + (i + 1)), keyDefaults[i], wildcard("key.categories.<id>"));
            spellBinds[i] = new SpellBinding(new KeyLock(bind), i);
        }

    }

    public static void register(Consumer<KeyBinding> registry) {
        for (KeyBinding key : keySpells) {
            registry.accept(key);
        }
    }

    public static Optional<SpellBinding> getActiveBind() {
        KeyLock.tickAll();
        return Arrays.stream(spellBinds)
                .filter(bind -> bind.key.isPress())
                .findFirst();
    }

    public static void pollActive(Consumer<SpellBinding> consumer) {
        getActiveBind().ifPresent(consumer);
    }

    public record SpellBinding(KeyLock key, int ordinal) {}


}
