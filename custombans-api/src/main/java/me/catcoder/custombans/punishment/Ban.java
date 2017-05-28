package me.catcoder.custombans.punishment;

import me.catcoder.custombans.language.MessageFormatter;

import java.util.UUID;

/**
 * Created by Ruslan on 25.04.2017.
 */
public class Ban extends Punishment {

    public Ban(String reason, UUID uniqueId, String banner) {
        super(reason, uniqueId, banner);
    }

    @Override
    public String getMessage() {
        return MessageFormatter.create()
                .addVariable("banner", getBanner())
                .addVariable("reason", getReason())
                .format("formats.ban_format");
    }
}
