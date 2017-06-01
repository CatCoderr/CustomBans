package me.catcoder.custombans.punishment;

import me.catcoder.custombans.database.AbstractDatabase;
import me.catcoder.custombans.language.MessageFormatter;

import java.util.UUID;

/**
 * Created by Ruslan on 25.04.2017.
 */
public class Ban extends Punishment {

    public Ban(String reason, String name, String banner, String params) {
        super(reason, name, banner, params);
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
                "INSERT INTO `bans` (name, banner, reason, params, time) VALUES (?, ?, ?, ?, ?)",
                getName().toLowerCase(), getBanner(), getReason(), params == null ? "" : params, 0);
    }
}
