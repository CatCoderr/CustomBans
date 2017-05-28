package me.catcoder.custombans.command.utility;

import com.google.common.base.Throwables;
import lombok.Getter;
import lombok.NonNull;
import me.catcoder.custombans.actor.Actor;

import java.util.*;

/**
 * Created by Ruslan on 25.04.2017.
 * Used for commands registration.
 */
@Getter
public abstract class CommandExecutor {

    private final Map<String, CommandBase> commandMap = new LinkedHashMap<>();


    public void registerCommand(@NonNull CommandBase commandBase) {
        commandMap.put(commandBase.getName(), commandBase);
    }

    public void unregisterCommand(String name) {
        commandMap.remove(name);
    }


    public boolean execute(Actor sender, String line) throws CommandPermissionException {
        String[] split = line.split(" ");
        String command = split[0];
        if (getCommand(command) == null) return false;

        CommandBase commandBase = getCommand(command);

        if (commandBase.getPermission() != null && !sender.hasPermission(commandBase.getPermission()))
            throw new CommandPermissionException(commandBase);

        Deque<String> args = new ArrayDeque<>(Arrays.asList(Arrays.copyOfRange(split, 1, split.length)));
        try {
            return commandBase.execute(sender, args, fillCommandLocals(new CommandLocals()));
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }

    public abstract CommandLocals fillCommandLocals(CommandLocals locals);

    public CommandBase getCommand(String name) {
        return commandMap.values()
                .stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(name) ||
                        Arrays.asList(cmd.getAliases()).contains(name.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

}
