package me.catcoder.custombans.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by CatCoder on 28.05.2017.
 */
public class ConfigurationLoader {

    private final ThreadLocal<Yaml> yaml = new ThreadLocal<Yaml>() {
        @Override
        protected Yaml initialValue() {
            Representer representer = new Representer() {
                {
                    representers.put(Configuration.class, data -> represent(((Configuration) data).self));
                }
            };

            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            return new Yaml(new Constructor(), representer, options);
        }
    };

    public void save(Configuration config, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            save(config, writer);
        }
    }

    public void save(Configuration config, Writer writer) {
        yaml.get().dump(config.self, writer);
    }

    public Configuration load(File file) throws IOException {
        return load(file, null);
    }

    public Configuration load(File file, Configuration defaults) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            return load(reader, defaults);
        }
    }

    public Configuration load(Reader reader) {
        return load(reader, null);
    }

    public Configuration load(Reader reader, Configuration defaults) {
        Map<String, Object> map = yaml.get().loadAs(reader, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new Configuration(map, defaults);
    }

    public Configuration load(InputStream is) {
        return load(is, null);
    }

    public Configuration load(InputStream is, Configuration defaults) {
        Map<String, Object> map = yaml.get().loadAs(is, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new Configuration(map, defaults);
    }

    public Configuration load(String string) {
        return load(string, null);
    }

    public Configuration load(String string, Configuration defaults) {
        Map<String, Object> map = yaml.get().loadAs(string, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new Configuration(map, defaults);
    }
}
