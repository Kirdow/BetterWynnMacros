package com.kirdow.wynnmacros.config;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class ModConfig {

    @Comment("Base Delay")
    public long baseDelay = 100L;

    @Comment("Force Cast")
    public ForceCastType forceCast = ForceCastType.OFF;
    @Comment("Smart Cast")
    public boolean smartCast = true;

    @Comment("Allow Interaction")
    public boolean allowInteraction = false;

}
