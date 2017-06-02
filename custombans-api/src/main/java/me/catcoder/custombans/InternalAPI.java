package me.catcoder.custombans;

import lombok.RequiredArgsConstructor;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.api.BanManager;
import me.catcoder.custombans.api.CustomAPI;
import me.catcoder.custombans.punishment.Ban;
import me.catcoder.custombans.punishment.Mute;
import me.catcoder.custombans.punishment.TempBan;
import me.catcoder.custombans.punishment.TempMute;
import me.catcoder.custombans.storage.PunishmentStorage;

/**
 * @author CatCoder
 */
@RequiredArgsConstructor
public class InternalAPI implements CustomAPI {

    private final CustomBans plugin;

    @Override
    public boolean isBanned(String nickname) {
        return getBan(nickname) != null;
    }

    @Override
    public boolean isTempBanned(String nickname) {
        return isBanned(nickname) && getBan(nickname) instanceof TempBan;
    }

    @Override
    public boolean isMuted(String nickname) {
        return getMute(nickname) != null;
    }

    @Override
    public boolean isTempMuted(String nickname) {
        return isMuted(nickname) && getMute(nickname) instanceof TempMute;
    }

    @Override
    public Mute getMute(String nickname) {
        return plugin.getBanManager().getMute(plugin.getActor(nickname));
    }

    @Override
    public TempMute getTempMute(String nickname) {
        if (!isTempMuted(nickname)) return null;
        return (TempMute) getMute(nickname);
    }

    @Override
    public Ban getBan(String nickname) {
        return plugin.getBanManager().getBan(plugin.getActor(nickname));
    }

    @Override
    public TempBan getTempBan(String nickname) {
        if (!isTempBanned(nickname)) return null;
        return (TempBan) getBan(nickname);
    }

    @Override
    public PunishmentStorage getPunishmentStorage() {
        return plugin.getStorage();
    }

    @Override
    public BanManager getBanManager() {
        return plugin.getBanManager();
    }

    @Override
    public Platform getPlatform() {
        return plugin.getPlatform();
    }

    @Override
    public Actor getActor(String nickname) {
        return plugin.getActor(nickname);
    }
}
