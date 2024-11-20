package com.kirdow.wynnmacros.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.kirdow.wynnmacros.WynnMacros.wildcard;

public class ConfigScreen {

    public static Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable(wildcard("config.<id>.base.modname")))
                .setDoesConfirmSave(true)
                .transparentBackground();

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        builder.getOrCreateCategory(Text.translatable(wildcard("config.<id>.section.general")))
                .addEntry(entryBuilder.startSelector(Text.translatable(wildcard("config.<id>.general.force.short")), Arrays.stream(ForceCastType.values()).map(p -> p.getDisplay()).collect(Collectors.toUnmodifiableList()).toArray(new String[0]), ConfigManager.get().forceCast.getDisplay())
                        .setDefaultValue(ForceCastType.OFF.getDisplay())
                        .setTooltip(Text.translatable(wildcard("config.<id>.general.force.long")))
                        .setSaveConsumer(value -> ConfigManager.get().forceCast = ForceCastType.getByTitle(value.substring(2)))
                        .build())
                .addEntry(entryBuilder.startBooleanToggle(Text.translatable(wildcard("config.<id>.general.smart.short")), ConfigManager.get().smartCast)
                        .setDefaultValue(true)
                        .setTooltip(Text.translatable(wildcard("config.<id>.general.smart.long")))
                        .setSaveConsumer(value -> ConfigManager.get().smartCast = value)
                        .build())
                .addEntry(entryBuilder.startBooleanToggle(Text.translatable(wildcard("config.<id>.general.interact.short")), ConfigManager.get().allowInteraction)
                        .setDefaultValue(true)
                        .setTooltip(Text.translatable(wildcard("config.<id>.general.interact.long")))
                        .setSaveConsumer(value -> ConfigManager.get().allowInteraction = value)
                        .build());


        builder.getOrCreateCategory(Text.translatable(wildcard("config.<id>.section.timings")))
                .addEntry(entryBuilder.startLongSlider(Text.translatable(wildcard("config.<id>.timings.base.short")), ConfigManager.get().baseDelay, 10L, 1000L)
                        .setDefaultValue(100L)
                        .setTooltip(Text.translatable(wildcard("config.<id>.timings.base.long")))
                        .setSaveConsumer(value -> ConfigManager.get().baseDelay = value)
                        .build());

        builder.setSavingRunnable(ConfigManager::saveConfig);

        return builder.build();
    }
}
