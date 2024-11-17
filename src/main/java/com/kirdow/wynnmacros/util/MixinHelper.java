package com.kirdow.wynnmacros.util;

import com.kirdow.wynnmacros.mixin.InGameHudAccessor;
import com.kirdow.wynnmacros.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

public class MixinHelper {

    public static String getName(KeyBinding bind) {
        return ((KeyBindingAccessor)bind).getKey();
    }

    public static Text getOverlayText() {
        var hud = MinecraftClient.getInstance().inGameHud;
        return ((InGameHudAccessor)hud).getOverlayText();
    }

    public static int getOverlayTime() {
        var hud = MinecraftClient.getInstance().inGameHud;
        return ((InGameHudAccessor)hud).getOverlayTime();
    }

    public static String getTargetKey() {
        return WynnHelper.isBow() ? "key.attack" : "key.use";
    }

}
