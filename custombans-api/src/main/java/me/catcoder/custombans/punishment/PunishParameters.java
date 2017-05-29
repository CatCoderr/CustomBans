package me.catcoder.custombans.punishment;

import com.sk89q.CommandContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CatCoder on 29.05.2017.
 */
public class PunishParameters {

    private Map<Object, Object> parameters = new HashMap<>();

    public Map<Object, Object> getParameters() {
        return parameters;
    }

    public boolean hasParameter(Object key) {
        return parameters.containsKey(key);
    }

    public <T> T get(Object key) {
        return (T) parameters.get(key);
    }

    public Object put(Object key, Object value) {
        return parameters.put(key, value);
    }

    public static PunishParameters of(CommandContext commandContext) {
        PunishParameters parameters = new PunishParameters();

        for (char flag : commandContext.getFlags()) {
            parameters.put(flag, flag);
        }
        commandContext.getValueFlags().forEach(parameters::put);
        return parameters;
    }
}
