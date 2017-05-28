package me.catcoder.custombans.limit;

import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.command.utility.CommandLocals;
import me.catcoder.custombans.punishment.ActionType;

/**
 * Created by CatCoder on 27.05.2017.
 */
public interface Limiter {

    void checkLimit(Actor sender, ActionType type, int time, CommandLocals locals);

}
