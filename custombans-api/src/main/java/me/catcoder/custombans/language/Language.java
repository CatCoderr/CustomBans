package me.catcoder.custombans.language;

import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by CatCoder on 28.05.2017.
 */
public class Language {

    private Configuration configuration;
    private final CustomBans plugin;
    private final File file;

    public Language(File file, CustomBans plugin) throws IOException {
        this.file = file;
        this.configuration = plugin.getConfigurationLoader().load(file);
        this.plugin = plugin;
    }

    public void reload() throws IOException {
        this.configuration = plugin.getConfigurationLoader().load(file);
    }

    public String translate(String path, Map<String, String> variables) {
        String message = ColorFormat.translateAlternateColorCodes('&', this.configuration.getString(path));

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message == null ? "Message not found in path " + path : message;
    }
}
