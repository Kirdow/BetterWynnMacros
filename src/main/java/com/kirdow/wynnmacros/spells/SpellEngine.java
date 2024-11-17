package com.kirdow.wynnmacros.spells;

import com.kirdow.wynnmacros.Logger;
import com.kirdow.wynnmacros.input.KeyBindings.SpellBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

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

    private void send(Packet<?> packet) {
        try {
            mc()
                    .getNetworkHandler()
                    .sendPacket(packet);
        } catch (Exception ex) {
            Logger.error("Failed to send packet: %s", ex.toString());
        }
    }

    private void actuator(Boolean state) {
        send((state ^ shouldInvertSpellCast())
                ? new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, mc().player.getYaw(), mc().player.getPitch())
                : new HandSwingC2SPacket(Hand.MAIN_HAND));
    }

    public static void post(SpellBinding bind) {
        get().useSpell(bind);
    }

    private static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    private static boolean shouldInvertSpellCast() {
        return mc()
                .player
                .getMainHandStack()
                .getTooltip(Item.TooltipContext.DEFAULT, mc().player, TooltipType.BASIC)
                .stream()
                .anyMatch(p -> p.getString().contains("Archer/Hunter"));
    }

}
