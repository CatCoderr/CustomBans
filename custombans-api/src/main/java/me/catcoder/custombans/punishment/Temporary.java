package me.catcoder.custombans.punishment;

/**
 * Created by Ruslan on 25.04.2017.
 */
public interface Temporary {

    long getExpires();

    boolean hasExpired();
}
