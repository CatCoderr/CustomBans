package me.catcoder.custombans.punishment;

/**
 * Created by CatCoder on 27.05.2017.
 */
public enum ActionType {

    BAN, TEMPBAN, MUTE, TEMPMUTE;

    public boolean isTemporary() {
        return this == TEMPMUTE || this == TEMPBAN;
    }

    public boolean isPermament() {
        return this == MUTE || this == BAN;
    }
}
