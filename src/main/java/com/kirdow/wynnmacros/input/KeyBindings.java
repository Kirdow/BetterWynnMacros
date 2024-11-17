package com.kirdow.wynnmacros.input;

import net.minecraft.client.option.KeyBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.kirdow.wynnmacros.WynnMacros.wildcard;

public class KeyBindings {

    private static SpellBinding[] spellBinds;
    public static KeyBinding[] keySpells;
    public static final char[] keyDefaults = new char[] {'R', 'F', 'Q', 'V' };

    public static void init() {
        keySpells = new KeyBinding[keyDefaults.length];
        spellBinds = new SpellBinding[keyDefaults.length];
        for (int i = 0; i < keyDefaults.length; i++) {
            KeyBinding bind = keySpells[i] = new KeyBinding(wildcard("key.<id>.spell." + (i + 1)), keyDefaults[i], wildcard("key.categories.<id>"));
            spellBinds[i] = new SpellBinding(bind, i);
        }

    }

    public static void register(Consumer<KeyBinding> registry) {
        for (KeyBinding key : keySpells) {
            registry.accept(key);
        }
    }

    public static List<SpellBinding> getActiveBinds() {
        return Arrays.stream(spellBinds).filter(p -> p.entry().wasPressed()).collect(Collectors.toUnmodifiableList());
    }

    public static void pollActive(Consumer<SpellBinding> consumer) {
        for (var active = getActiveBinds(); active.size() > 0; active = getActiveBinds()) {
            for (SpellBinding binding : active) {
                if (consumer != null)
                    consumer.accept(binding);
            }
        }
    }

    public record SpellBinding(KeyBinding entry, int ordinal) {
    }


}
