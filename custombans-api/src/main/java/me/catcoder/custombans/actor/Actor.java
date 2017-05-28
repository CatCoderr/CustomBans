package me.catcoder.custombans.actor;


import me.catcoder.custombans.punishment.Ban;
import me.catcoder.custombans.punishment.Mute;

import java.util.UUID;

/**
 * Created by CatCoder on 27.05.2017.
 */
public interface Actor {

    void printMessage(String message, Object... objects);

    boolean hasPermission(String permission);

    String getName();

    UUID getUniqueId();

    Ban getBan();

    Mute getMute();
}
