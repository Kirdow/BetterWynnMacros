package com.kirdow.wynnmacros.util;

import com.kirdow.wynnmacros.util.AsyncEngine;

import com.kirdow.wynnmacros.mixin.InGameHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;

import java.util.Arrays;

public class WynnHelper {

    public static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    public static ClientPlayerEntity player() {
        return mc().player;
    }

    public static ClientWorld world() {
        return mc().world;
    }

    public static int slotNumber() {
        return player().getInventory().selectedSlot;
    }

    public static AsyncEngine swapSlotAsync() {
        int slot = slotNumber();
        return AsyncEngine.start()
                .after(50L, () -> sendSlot(slot + 1))
                .after(50L, () -> sendSlot(slot))
                .sleep(50L);
    }

    public static AsyncEngine checkSpellSequence() {
        return hasSpellQueued() ? swapSlotAsync() : AsyncEngine.start();
    }

    private static void sendPacket(Packet<?> packet) {
        player()
                .networkHandler
                .sendPacket(packet);
    }

    public static void sendSlot(int i) {
        int slot = (i + 9) % 9;
        if (slot < 0) slot = 0;

        sendPacket(new UpdateSelectedSlotC2SPacket(slot));
    }

    public static void sendUse() {
        sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, player().getYaw(), player().getPitch()));
    }

    public static void sendAttack() {
        sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
    }

    private static boolean hasSpellQueued() {
        var inGame = mc().inGameHud;
        String text = ((InGameHudAccessor)inGame).getOverlayText() != null && ((InGameHudAccessor)inGame).getOverlayTime() > 0 ? ((InGameHudAccessor)inGame).getOverlayText().getString() : "";
        return Arrays.stream(BIND_CHARS.split("(?<=.)")).anyMatch(p -> text.contains(p));
    }

    private static final String BIND_CHARS = "\ue010\ue011\ue012";

}
