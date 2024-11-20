package com.kirdow.wynnmacros.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kirdow.wynnmacros.Logger;
import com.kirdow.wynnmacros.WynnMacros;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static File CONFIG_FILE;

    private static ModConfig config;

    public static ModConfig get() {
        return config;
    }

    public static void loadConfig() {
        if (CONFIG_FILE == null) {
            CONFIG_FILE = new File(WynnMacros.getModPath().toAbsolutePath().toFile(), "config.json");
        }

        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, ModConfig.class);
            } catch (Exception ex) {
                Logger.exception(ex, WynnMacros.wildcard("Failed to load <name> config"));
                config = new ModConfig();
            }
        } else {
            config = new ModConfig();
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException ex) {
            Logger.exception(ex, WynnMacros.wildcard("Failed to save <name> config"));
        }
    }
}
