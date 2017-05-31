package me.catcoder.custombans.language;

import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.config.Configuration;
import me.catcoder.custombans.utility.ConfigUtility;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by CatCoder on 28.05.2017.
 */
public class Language {

    public static final Pattern PLACEHOLDER_MATCH = Pattern.compile("\\{([^\\}]+)\\}");

    private Configuration configuration;
    private final CustomBans plugin;
    private final File file;

    public Language(File file, CustomBans plugin) throws IOException {
        this.file = file;
        this.configuration = ConfigUtility.get(plugin.getConfigurationLoader(), file);
        this.plugin = plugin;
    }

    public void reload() throws IOException {
        this.configuration = plugin.getConfigurationLoader().load(file);
    }

    public String translate(String path, Map<String, String> variables) {
        if (this.configuration.getString(path) == null) {
            return "Message not found at path: " + path;
        }
        String message = ColorFormat.translateAlternateColorCodes('&', this.configuration.getString(path));
        Matcher matcher = PLACEHOLDER_MATCH.matcher(message);
        StringBuffer output = new StringBuffer();

        while (matcher.find()) {
            String variable = matcher.group(1);
            String replacement = variables.get(variable.toLowerCase());
            if (replacement != null) {
                matcher.appendReplacement(output, Matcher.quoteReplacement(replacement));
            }
        }
        return output.toString();
    }
}
