package me.catcoder.custombans.punishment;

import me.catcoder.custombans.database.AbstractDatabase;
import me.catcoder.custombans.language.MessageFormatter;
import me.catcoder.custombans.utility.StringUtil;
import me.catcoder.custombans.utility.TimeUtility;

import java.util.UUID;

/**
 * Created by Ruslan on 25.04.2017.
 */
public class TempBan extends Ban implements Temporary {

    private final long expires;

    public TempBan(String reason, String name, String banner, long expires, String params) {
        super(reason, name, banner, params);
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
        return StringUtil.joinString(MessageFormatter.create()
                .addVariable("banner", getBanner())
                .addVariable("reason", getReason())
                .addVariable("until", TimeUtility.timeUntilNow(getExpires()))
                .formatList("formats.tempban_format"), "\n", 0);
    }

    @Override
    public void insertInto(AbstractDatabase database) {
        database.execute(
                "INSERT INTO `mutes` (name, banner, reason, params, time) VALUES (?, ?, ?, ?, ?)",
                getName().toLowerCase(), getBanner(), getReason(), params == null ? "" : params, getExpires());
    }
}
