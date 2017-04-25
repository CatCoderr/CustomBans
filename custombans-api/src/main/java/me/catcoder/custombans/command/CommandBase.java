package me.catcoder.custombans.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Ruslan on 25.04.2017.
 */
@AllArgsConstructor
@Getter
public abstract class CommandBase<T> {

    private final String[] aliases;
    private final String name;
    private final String permission;

    public abstract boolean execute(T player, String[] args);


}
