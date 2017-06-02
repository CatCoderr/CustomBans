package me.catcoder.custombans.punishment;

import me.catcoder.custombans.database.AbstractDatabase;
import me.catcoder.custombans.language.MessageFormatter;
import me.catcoder.custombans.utility.StringUtil;

import java.util.List;
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
        return StringUtil.joinString(MessageFormatter.create()
                .addVariable("banner", getBanner())
                .addVariable("reason", getReason())
                .formatList("formats.ban_format"), "\n", 0);
    }

    @Override
    public void insertInto(AbstractDatabase database) {
        database.execute(
                "INSERT INTO `bans` (name, banner, reason, params, time) VALUES (?, ?, ?, ?, ?)",
                getName().toLowerCase(), getBanner(), getReason(), params == null ? "" : params, 0);
    }
}
