package me.catcoder.custombans.limit;

import me.catcoder.custombans.actor.Actor;

/**
 * Created by CatCoder on 28.05.2017.
 */

public interface LimitInfo {


    Result getResult();

    boolean allowed();

    String getDisallowMessage();

    boolean canAccess(int time, Actor actor);

    public static enum Result {
        ALLOWED, DISALLOWED
    }
}
