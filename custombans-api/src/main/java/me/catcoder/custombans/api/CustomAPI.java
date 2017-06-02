package me.catcoder.custombans.api;

import me.catcoder.custombans.Platform;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.punishment.*;
import me.catcoder.custombans.storage.PunishmentStorage;

/**
 * @author CatCoder
 */
public interface CustomAPI {

    boolean isBanned(String nickname);

    boolean isTempBanned(String nickname);

    boolean isMuted(String nickname);

    boolean isTempMuted(String nickname);

    Mute getMute(String nickname);

    TempMute getTempMute(String nickname);

    Ban getBan(String nickname);

    TempBan getTempBan(String nickname);

    PunishmentStorage getPunishmentStorage();

    BanManager getBanManager();

    Platform getPlatform();

    Actor getActor(String nickname);
}
