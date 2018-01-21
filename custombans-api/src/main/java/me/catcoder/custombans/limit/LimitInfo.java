package me.catcoder.custombans.limit;

import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.limit.cooldown.Cooldown;

/**
 * Created by CatCoder on 28.05.2017.
 */

public interface LimitInfo {

    boolean canAccess(long time);

    boolean canBan(Actor target);

    long getAllowedTime();

}
