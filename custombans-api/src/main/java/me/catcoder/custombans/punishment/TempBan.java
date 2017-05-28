package me.catcoder.custombans.punishment;

import me.catcoder.custombans.language.MessageFormatter;
import me.catcoder.custombans.utility.TimeUtility;

import java.util.UUID;

/**
 * Created by Ruslan on 25.04.2017.
 */
public class TempBan extends Ban implements Temporary {

    private final long expires;

    public TempBan(String reason, UUID uniqueId, String banner, long expires) {
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

    @Override
    public String getMessage() {
        return MessageFormatter.create()
                .addVariable("banner", getBanner())
                .addVariable("reason", getReason())
                .addVariable("unit", TimeUtility.timeUntilNow(getExpires(), true))
                .format("formats.tempban_format");
    }
}
