package me.catcoder.custombans.bukkit;

import com.google.common.base.Preconditions;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.punishment.Ban;
import me.catcoder.custombans.punishment.Mute;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author CatCoder
 */
public class BukkitActor implements Actor {

    private final Player player;

    public BukkitActor(Player player) {
        this.player = player;
    }

    @Override
    public void printMessage(String message, Object... objects) {
        player.sendMessage(String.format(message, objects));
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public Ban getBan() {
        return CustomBans.getInstance().getBanManager().getBan(this);
    }

    @Override
    public Mute getMute() {
        return CustomBans.getInstance().getBanManager().getMute(this);
    }

}
