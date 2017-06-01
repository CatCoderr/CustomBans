package me.catcoder.custombans.punishment;

import me.catcoder.custombans.database.AbstractDatabase;
import me.catcoder.custombans.language.MessageFormatter;

import java.util.UUID;

/**
 * Created by Ruslan on 25.04.2017.
 */
public class Ban extends Punishment {

    public Ban(String reason, UUID uniqueId, String banner, String params) {
        super(reason, uniqueId, banner, params);
    }

    @Override
    public String getMessage() {
        return MessageFormatter.create()
                .addVariable("banner", getBanner())
                .addVariable("reason", getReason())
                .format("formats.ban_format");
    }

    @Override
    public void insertInto(AbstractDatabase database) {
        database.execute(
                "INSERT INTO `bans` (uuid, banner, reason, params, time) VALUES (?, ?, ?, ?, ?)",
                getUniqueId().toString(), getBanner(), getReason(), params == null ? "" : params, 0);
    }
}
