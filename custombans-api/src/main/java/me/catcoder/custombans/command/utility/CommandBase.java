package me.catcoder.custombans.command.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.catcoder.custombans.actor.Actor;

import java.util.Deque;

/**
 * Created by Ruslan on 25.04.2017.
 */
@AllArgsConstructor
@Getter
public abstract class CommandBase {

    private final String[] aliases;
    private final String name;
    private final String permission;
    private final String usage;

    public abstract boolean execute(Actor actor, Deque<String> args, CommandLocals locals);


}
