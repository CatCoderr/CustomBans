package me.catcoder.custombans.bukkit;

import com.google.common.base.Preconditions;
import lombok.Getter;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.punishment.Ban;
import me.catcoder.custombans.punishment.Mute;
import me.catcoder.custombans.utility.UUIDFetcher;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author CatCoder
 */
public class BukkitActor implements Actor {

    @Getter
    private Player player;
    private String name;

    public BukkitActor(Player player) {
        this.player = player;
        this.name = player.getName();
    }

    public BukkitActor(String name) {
        this.name = name;
    }

    @Override
    public void printMessage(String message, Object... objects) {
        checkState();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(message, objects)));
    }

    @Override
    public boolean hasPermission(String permission) {
        checkState();
        return player.hasPermission(permission);
    }

    private void checkState() {
        Preconditions.checkState(isOnline(), "Player is offline.");
    }

    @Override
    public boolean isOnline() {
        return player != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUniqueId() {
        try {
            return UUIDFetcher.getUUIDOf(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
