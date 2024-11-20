package com.kirdow.wynnmacros.spells;

import com.kirdow.wynnmacros.Logger;
import com.kirdow.wynnmacros.util.WynnHelper;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

import java.util.Optional;

public enum SpellKey {

    LEFT("Left", false),
    RIGHT("Right", true);

    private final String name;
    private final boolean state;

    SpellKey(String name, boolean state) {
        this.name = name;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public boolean getState() {
        return state;
    }

    public <T> T conditional(T ifTrue, T ifFalse) {
        return state ? ifTrue : ifFalse;
    }

    public Packet<?> createPacket() {
        if (state) {
            Logger.dev("Right");
            return new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, WynnHelper.player().getYaw(), WynnHelper.player().getPitch());
        } else {
            Logger.dev("Left");
            return new HandSwingC2SPacket(Hand.MAIN_HAND);
        }
    }

    public SpellKey mask(boolean flag) {
        return get(state ^ flag);
    }

    public static SpellKey get(boolean state) {
        return state ? RIGHT : LEFT;
    }

    public static Optional<SpellKey> get(SpellSequence.SpellIcon icon) {
        return switch (icon) {
            case LEFT -> Optional.of(LEFT);
            case RIGHT -> Optional.of(RIGHT);
            default -> Optional.empty();
        };
    }

}
