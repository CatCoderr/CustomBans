package me.catcoder.custombans.language;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import me.catcoder.custombans.CustomBans;

import java.util.Map;

/**
 * Created by CatCoder on 28.05.2017.
 */
public class MessageFormatter {

    private Map<String, String> variables = Maps.newHashMap();

    public MessageFormatter addVariable(String name, String value) {
        Preconditions.checkArgument(name != null || value != null, "Invalid request.");

        this.variables.put(name, value);
        return this;
    }

    public String format(String path) {
        return CustomBans.getInstance().getLanguage().translate(path, variables);
    }

    public static MessageFormatter create() {
        return new MessageFormatter();
    }
}
