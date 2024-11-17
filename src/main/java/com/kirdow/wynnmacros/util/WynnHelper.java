package com.kirdow.wynnmacros.util;

import com.kirdow.wynnmacros.Logger;
import com.kirdow.wynnmacros.config.ConfigManager;

import com.kirdow.wynnmacros.mixin.InGameHudAccessor;
import com.kirdow.wynnmacros.spells.SpellSequence;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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

    private static Consumer<AsyncEngine> signal;

    public static void tick() {
        if (signal != null && SpellSequence.extractSequence().isEmpty()) {
            signal.accept(AsyncEngine.start());
            signal = null;
        }
    }

    public static void tryRunWait(Consumer<AsyncEngine> callback) {
        switch (ConfigManager.get().forceCast) {
            case OFF:
            case BLOCKING:
                callback.accept(AsyncEngine.start());
                break;
            case WAIT:
                signal = callback;
                break;
        }
    }

    private static boolean[] getSpellQueue() {
        var inGame = mc().inGameHud;
        String text = ((InGameHudAccessor)inGame).getOverlayText() != null && ((InGameHudAccessor)inGame).getOverlayTime() > 0 ? ((InGameHudAccessor)inGame).getOverlayText().getString() : "";
        Logger.debug("Found test \"%s\" at %d ticks remaining", text, (int)getOverlayLeft());
        List<Boolean> sequence = new ArrayList<>();
        for (char c : text.toCharArray()) {
            if (c == BIND_CHARS.charAt(1)) {
                Logger.debug("Found RMB");
                sequence.add(true);
            } else if (c == BIND_CHARS.charAt(0)) {
                Logger.debug("Found LMB");
                sequence.add(false);
            }
        }

        boolean[] result = new boolean[sequence.size()];
        for (int i = 0; i < result.length; i++) {
            boolean activeButton = sequence.get(i) ^ isBow();

            Logger.debug("Button: %s", activeButton ? "x" : "o");

            result[i] = activeButton;
        }

        return result;
    }

    public static Optional<boolean[]> getMatchingSpellQueue(boolean[] spell) {
        List<Boolean> queue = SpellSequence.extractSequence().orElse(Collections.emptyList());

        if (queue.isEmpty()) {
            Logger.debug("Queue is empty");
            return Optional.of(spell);
        }

        int len = Math.min(spell.length, queue.size());
        Logger.debug("Queue len: %d", len);
        for (int i = 0; i < Math.min(spell.length, queue.size()); i++) {
            if (spell[i] != queue.get(i)) {
                Logger.debug("Queue index %d differ", i);
                return Optional.empty();
            }
        }

        Logger.debug("Queue match");

        if (!ConfigManager.get().smartCast) {
            Logger.debug("No smart cast. Return spell (len = %d)", spell.length);
            return Optional.of(spell);
        }

        boolean[] result = new boolean[spell.length - queue.size()];
        if (result.length == 0) {
            Logger.debug("Detected completed spell. Returning full spell (len = %d)", spell.length);
            return Optional.of(spell);
        }

        Logger.debug("Skipping %d, creating result of (len = %d)", queue.size(), result.length);

        for (int i = 0; i < result.length; i++) {
            boolean next = result[i] = spell[i + queue.size()];
            Logger.debug("Using %s for index %d of spell", next ? "x" : "o", i + queue.size());
        }

        Logger.debug("Returning spell (len = %d)", result.length);
        return Optional.of(result);
    }

    private static void sendPacket(Packet<?> packet) {
        player()
                .networkHandler
                .sendPacket(packet);
    }

    public static void sendUse() {
        sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, player().getYaw(), player().getPitch()));
    }

    public static void sendAttack() {
        sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
    }

    private static long getOverlayLeft() {
        var hud = mc().inGameHud;
        if (hud == null) return 0L;
        return ((InGameHudAccessor)hud).getOverlayText() != null ? (long)((InGameHudAccessor)hud).getOverlayTime() : 0L;
    }

    public static boolean isBow() {
        return isClass("Archer/Hunter");
    }

    public static boolean isWeapon() {
        return isClass("Archer/Hunter", "Warrior/Knight", "Mage/Wizard", "Assassin/Ninja", "Shaman/Skyseer");
    }

    public static boolean isClass(String...search) {
        var player = mc().player;
        if (player == null) return false;
        var item = player.getMainHandStack();
        if (item == null) return false;
        return item
                .getTooltip(Item.TooltipContext.DEFAULT, mc().player, TooltipType.BASIC)
                .stream()
                .anyMatch(p -> {
                    for (String str : search) {
                        if (p.getString().contains(str)) return true;
                    }
                    return false;
                });
    }


    private static final String BIND_CHARS = "\ue010\ue011";

}
