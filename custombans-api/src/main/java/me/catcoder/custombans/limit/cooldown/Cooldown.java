package me.catcoder.custombans.limit.cooldown;

public class Cooldown {

    private final long time;
    private final String key;

    public long getTime() {
        return time;
    }

    public String getKey() {
        return key;
    }

    boolean hasExpired() {
        return getRemaining() <= 0;
    }

    public long getRemaining() {
        return time - System.currentTimeMillis();
    }

    public Cooldown(long time, String key) {
        this.time = time;
        this.key = key;
    }

}
