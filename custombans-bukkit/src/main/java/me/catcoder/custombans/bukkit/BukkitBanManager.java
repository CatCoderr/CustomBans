package me.catcoder.custombans.bukkit;

import com.google.common.collect.Iterables;
import lombok.RequiredArgsConstructor;
import me.catcoder.custombans.BanManager;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.punishment.*;

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
        target.printMessage("Banned");
        customBans.getStorage().add(new Ban(reason, target.getUniqueId(), banner.getName(), null));
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
        return customBans.getStorage().getMutes().get(target.getUniqueId());
    }

    @Override
    public Ban getBan(Actor target) {
        return customBans.getStorage().getBans().get(target.getUniqueId());
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
}
