package me.catcoder.custombans.command.utility;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;

/**
 * Created by CatCoder on 28.05.2017.
 */
public class CommandLocals {

    @Getter
    private final Map<Class, Object> locals;

    public CommandLocals() {
        this(null);
    }

    public CommandLocals(Map<Class, Object> locals) {
        this.locals = locals == null ? Maps.newHashMap() : locals;
    }

    public <T> T get(Class<T> clazz) {
        return (T) locals.get(clazz);
    }

}

