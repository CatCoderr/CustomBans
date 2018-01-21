package me.catcoder.custombans.limit;

import com.sk89q.CommandLocals;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.punishment.ActionType;

/**
 * Created by CatCoder on 27.05.2017.
 */
public interface Limiter {

    void checkLimit(Actor sender, ActionType type, CommandLocals locals);

    void checkCooldown(Actor sender, CommandLocals locals, ActionType type);

    int getCustomPriority(Actor actor);

    void reload();
}
