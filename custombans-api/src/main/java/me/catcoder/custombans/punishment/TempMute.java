package me.catcoder.custombans.punishment;

import java.util.UUID;

/**
 * Created by Ruslan on 25.04.2017.
 */
public class TempMute extends Mute implements Temporary {

    private final long expires;

    public TempMute(String reason, UUID uniqueId, String banner, long expires) {
        super(reason, uniqueId, banner);
        this.expires = expires;
    }

    @Override
    public long getExpires() {
        return expires;
    }

    @Override
    public boolean hasExpired() {
        return System.currentTimeMillis() > expires;
    }
}
