package me.catcoder.custombans.limit;

import me.catcoder.custombans.actor.Actor;

/**
 * Created by CatCoder on 28.05.2017.
 */

public interface LimitInfo {

    boolean canAccess(long time);

    boolean canBan(Actor target);

    long getAllowedTime();

}
