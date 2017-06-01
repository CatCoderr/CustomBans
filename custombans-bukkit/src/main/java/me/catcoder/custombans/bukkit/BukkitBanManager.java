package me.catcoder.custombans.bukkit;

import com.google.common.collect.Iterables;
import me.catcoder.custombans.BanManager;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.punishment.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author CatCoder
 */
public class BukkitBanManager implements BanManager {

    private final CustomBans customBans;

    public BukkitBanManager(CustomBans customBans) {
        this.customBans = customBans;
    }

    @Override
    public void ban(Actor target, Actor banner, String reason, PunishParameters parameters) {
        customBans.getStorage().add(new Ban(reason, target.getName(), banner.getName(), null));
    }

    @Override
    public void tempban(Actor target, Actor banner, String reason, long time, PunishParameters parameters) {

    }

    @Override
    public void unban(Actor actor, PunishParameters parameters) {

    }

    @Override
    public void mute(Actor target, Actor banner, String reason, PunishParameters parameters) {

    }

    @Override
    public void tempmute(Actor target, Actor banner, String reason, long time, PunishParameters parameters) {

    }

    @Override
    public void unmute(Actor actor, PunishParameters parameters) {

    }

    @Override
    public Mute getMute(Actor target) {
        return customBans.getStorage().getMutes().get(target.getName().toLowerCase());
    }

    @Override
    public Ban getBan(Actor target) {
        return customBans.getStorage().getBans().get(target.getName().toLowerCase());
    }

    @Override
    public Iterable<Punishment> getPunishments() {
        return Iterables.concat(getBans(), getMutes());
    }

    @Override
    public Collection<Ban> getBans() {
        return customBans.getStorage().getBans().values();
    }

    @Override
    public Collection<Mute> getMutes() {
        return customBans.getStorage().getMutes().values();
    }

    @Override
    public boolean clear(ActionType type, Actor target) {
        return true;
    }

    private void announceMessage(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }
}
