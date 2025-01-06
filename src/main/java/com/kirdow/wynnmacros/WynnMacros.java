package com.kirdow.wynnmacros;

import com.kirdow.wynnmacros.config.ConfigManager;
import com.kirdow.wynnmacros.input.KeyBindings;
import com.kirdow.wynnmacros.spells.SpellEngine;
import com.kirdow.wynnmacros.util.PlayerHelper;
import com.kirdow.wynnmacros.util.WynnHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public class WynnMacros implements ClientModInitializer {
    public static final String MOD_ID = "ktnwynnmacros";
    public static final String MOD_NAME = "Better Wynn Macros";
    public static final String MOD_VERSION = "1.0.2";
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static Path modPath;

    @Override
    public void onInitializeClient() {
        Logger.setLogger(LOGGER::info, LOGGER::debug, LOGGER::error, LOGGER::warn, LOGGER::error);

        Logger.info("Preparing mod config directory");
        modPath = FabricLoader.getInstance().getConfigDir().resolve(WynnMacros.MOD_ID);
        File modFolder = modPath.toFile();
        if (!modFolder.exists()) {
            Logger.info("Creating config directory");
            if (!modFolder.mkdir())
                Logger.error("Failed creating config directory: %s", modFolder.getAbsolutePath());
        }

        PlayerHelper.init();

        KeyBindings.init();
        KeyBindings.register(KeyBindingHelper::registerKeyBinding);

        ConfigManager.loadConfig();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            WynnHelper.tick();
            KeyBindings.pollActive(SpellEngine::post);
        });
    }

    public static String wildcard(String format) {
        return format
                .replace("<id>", MOD_ID)
                .replace("<name>", MOD_NAME)
                .replace("<version>", MOD_VERSION);
    }

    public static Path getModPath() {
        return modPath;
    }
}
