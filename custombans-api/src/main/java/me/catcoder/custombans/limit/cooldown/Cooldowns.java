package me.catcoder.custombans.limit.cooldown;

import java.util.HashMap;
import java.util.Map;

public class Cooldowns {

    private Cooldowns() {

    }

    private static final Map<String, Cooldown> COOLDOWNS = new HashMap<>();

    public static Cooldown setCooldown(String key, long millis) {
        Cooldown cooldown = new Cooldown(System.currentTimeMillis() + millis, key);
        COOLDOWNS.put(key.toLowerCase(), cooldown);
        return cooldown;
    }

    public static Cooldown getCooldown(String key) {
        Cooldown cooldown = COOLDOWNS.get(key.toLowerCase());

        if (cooldown != null && cooldown.hasExpired()) {
            COOLDOWNS.remove(key.toLowerCase());
            cooldown = null;
        }

        return cooldown;
    }
}
