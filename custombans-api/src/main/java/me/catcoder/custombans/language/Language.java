package me.catcoder.custombans.language;

import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.config.Configuration;
import me.catcoder.custombans.utility.FileUtility;

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
    private String fileName;

    public Language(File file, CustomBans plugin) throws IOException {
        this.fileName = file.getName().replace(".yml", "");
        this.configuration = FileUtility.get(plugin.getConfigurationLoader(), file);
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
        matcher.appendTail(output);
        return output.toString();
    }

    @Override
    public String toString() {
        return fileName;
    }
}
