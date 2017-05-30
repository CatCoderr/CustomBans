package me.catcoder.custombans.punishment;

import me.catcoder.custombans.database.Database;
import me.catcoder.custombans.language.MessageFormatter;

import java.util.UUID;

/**
 * Created by Ruslan on 25.04.2017.
 */
public class Mute extends Punishment {

    public Mute(String reason, UUID uniqueId, String banner, String params) {
        super(reason, uniqueId, banner, params);
    }

    @Override
    public String getMessage() {
        return MessageFormatter.create()
                .addVariable("banner", getBanner())
                .addVariable("reason", getReason())
                .format("formats.mute_format");
    }

    @Override
    public void insertInto(Database database) {
        database.execute(
                "INSERT INTO `mutes` (uuid, banner, reason, params, time) VALUES (?, ?, ?, ?, ?)",
                getUniqueId().toString(), getBanner(), getReason(), params == null ? "" : params, 0L);
    }
}
