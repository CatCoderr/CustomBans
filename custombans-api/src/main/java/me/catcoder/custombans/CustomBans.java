package me.catcoder.custombans;

import com.google.common.base.Preconditions;
import lombok.experimental.Builder;
import me.catcoder.custombans.command.CommandExecutor;

/**
 * Created by Ruslan on 25.04.2017.
 */
@Builder
public class CustomBans {

    private static CustomBans instance;

    private final CommandExecutor commandExecutor;

    public CustomBans(CommandExecutor commandExecutor) {
        Preconditions.checkArgument(instance == null, "Instance already set.");
        this.commandExecutor = commandExecutor;
    }

    protected static void unregister() {
        instance = null;
    }
}
