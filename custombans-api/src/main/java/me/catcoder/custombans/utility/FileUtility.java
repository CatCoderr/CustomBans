package me.catcoder.custombans.utility;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.config.Configuration;
import me.catcoder.custombans.config.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CatCoder
 */
public final class FileUtility {

    private static Map<Configuration, File> configFiles = new HashMap<>();

    private FileUtility() {

    }

    public static Configuration get(@NonNull ConfigurationLoader loader, @NonNull File file) {
        try {
            if (!file.exists()) {
                copy(CustomBans.class.getClassLoader().getResourceAsStream(file.getName()), file.toPath());
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

    public static void copy(InputStream input, Path path) {
        try {
            Files.copy(input, path);
        } catch (IOException e) {
            throw new RuntimeException("Cannot copy file.");
        }
    }
}
