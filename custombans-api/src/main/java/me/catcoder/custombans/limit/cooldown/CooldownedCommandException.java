package me.catcoder.custombans.limit.cooldown;

import com.sk89q.CommandException;
import me.catcoder.custombans.language.MessageFormatter;

import java.util.concurrent.TimeUnit;

public class CooldownedCommandException extends CommandException {

    private final Cooldown cooldown;

    public CooldownedCommandException(Cooldown cooldown) {
        super();
        this.cooldown = cooldown;
    }

    @Override
    public String getMessage() {
        return MessageFormatter.create()
                .addVariable("time", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(cooldown.getRemaining())))
                .format("errors.cooldown");
    }
}
