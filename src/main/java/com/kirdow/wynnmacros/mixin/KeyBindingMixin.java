package com.kirdow.wynnmacros.mixin;

import com.kirdow.wynnmacros.config.ConfigManager;
import com.kirdow.wynnmacros.config.ForceCastType;
import com.kirdow.wynnmacros.util.MixinHelper;
import com.kirdow.wynnmacros.util.PlayerHelper;
import com.kirdow.wynnmacros.util.WynnHelper;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

    @Inject(method = "wasPressed()Z", at = @At(value = "RETURN"), cancellable = true)
    private void onConsumeClick(CallbackInfoReturnable<Boolean> ci) {
        if (!WynnHelper.isWeapon()) return;
        if (MixinHelper.getName((KeyBinding)(Object)this).equals(MixinHelper.getTargetKey()) && ConfigManager.get().forceCast == ForceCastType.BLOCKING) {
            if (PlayerHelper.canInteract(WynnHelper.player())) return;
            ci.setReturnValue(false);
        }
    }

    @Inject(method = "isPressed()Z", at = @At(value = "HEAD"), cancellable = true)
    private void onIsDown(CallbackInfoReturnable<Boolean> ci) {
        if (!WynnHelper.isWeapon()) return;
        if (MixinHelper.getName((KeyBinding)(Object)this).equals(MixinHelper.getTargetKey()) && ConfigManager.get().forceCast == ForceCastType.BLOCKING) {
            if (PlayerHelper.canInteract(WynnHelper.player())) return;
            ci.setReturnValue(false);
        }
    }

}
