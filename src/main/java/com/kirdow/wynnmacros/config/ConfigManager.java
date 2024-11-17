package com.kirdow.wynnmacros.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kirdow.wynnmacros.WynnMacros;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), WynnMacros.wildcard("<id>_config.json"));

    private static ModConfig config;

    public static ModConfig get() {
        return config;
    }

    public static void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, ModConfig.class);
            } catch (Exception ex) {
                ex.printStackTrace();
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
            ex.printStackTrace();
        }
    }
}
