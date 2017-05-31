package me.catcoder.custombans.utility;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.config.Configuration;
import me.catcoder.custombans.config.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CatCoder
 */
public final class ConfigUtility {

    private static Map<Configuration, File> configFiles = new HashMap<>();

    private ConfigUtility() {

    }

    public static Configuration get(@NonNull ConfigurationLoader loader, @NonNull File file) {
        try {
            if (!file.exists()) {
                //Copy file.
                Files.copy(CustomBans.class.getClassLoader().getResourceAsStream(file.getName()), file.toPath(), new CopyOption[0]);
            }
            Configuration config = loader.load(file);
            configFiles.put(config, file);
            return config;
        } catch (IOException ex) {
            throw new RuntimeException("Cannot load config.", ex);
        }
    }

    public static void save(@NonNull ConfigurationLoader loader, @NonNull Configuration config) {
        Preconditions.checkArgument(configFiles.containsKey(config), "Config is not loaded.");
        try {
            loader.save(config, configFiles.get(config));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
