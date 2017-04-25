package me.catcoder.custombans.command;

import com.google.common.base.Throwables;
import lombok.Getter;
import lombok.NonNull;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Ruslan on 25.04.2017.
 * Used for commands registration.
 */
@Getter
public abstract class CommandExecutor<T> {

    private final Map<String, CommandBase> commandMap = new LinkedHashMap<>();


    public void registerCommand(@NonNull CommandBase commandBase) {
        commandMap.put(commandBase.getName(), commandBase);
    }

    public void unregisterCommand(String name) {
        commandMap.remove(name);
    }

    //Must be implemented
    public abstract boolean canAccess(T player, String permission);

    public boolean execute(T sender, String line) throws CommandPermissionException {
        String[] split = line.split(" ");
        String command = split[0];
        if (getCommand(command) == null) return false;

        CommandBase commandBase = getCommand(command);

        if (commandBase.getPermission() != null && !canAccess(sender, commandBase.getPermission()))
            throw new CommandPermissionException(commandBase);

        try {
            return commandBase.execute(sender, Arrays.copyOfRange(split, 1, split.length));
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }

    public CommandBase getCommand(String name) {
        return commandMap.values()
                .stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(name) ||
                        Arrays.asList(cmd.getAliases()).contains(name.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

}
