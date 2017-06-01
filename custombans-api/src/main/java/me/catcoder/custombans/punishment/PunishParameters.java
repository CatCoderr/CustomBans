package me.catcoder.custombans.punishment;

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


    public static PunishParametersBuilder builder(PunishParameters handle) {
        return new PunishParametersBuilder(handle);
    }

    public static PunishParametersBuilder builder() {
        return builder(null);
    }

    public static class PunishParametersBuilder {

        private PunishParameters parameters;

        public PunishParametersBuilder(PunishParameters handle) {
            this.parameters = (handle == null ? new PunishParameters() : handle);
        }

        public PunishParametersBuilder append(Object key, Object value) {
            this.parameters.put(key, value);
            return this;
        }

        public PunishParameters build() {
            return parameters;
        }

    }
}
