package me.catcoder.custombans.api;

import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.punishment.*;

import java.util.Collection;

/**
 * Manager for manipulation of all punishments.
 *
 * @author CatCoder
 */
public interface BanManager {

    /**
     * Permanently bans player.
     *
     * @param target     - target player.
     * @param banner     - banner.
     * @param reason     - ban reason.
     * @param parameters - parameters
     */
    void ban(Actor target, Actor banner, String reason, PunishParameters parameters);

    /**
     * Temporary bans player.
     *
     * @param target     - target player.
     * @param banner     - banner.
     * @param reason     - ban reason.
     * @param parameters - parameters
     * @param time       - expires.
     */
    void tempban(Actor target, Actor banner, String reason, long time, PunishParameters parameters);

    /**
     * Unbans player.
     *
     * @param actor      - target player.
     * @param parameters - parameters
     */
    void unban(Actor actor, PunishParameters parameters);

    /**
     * Permanently mutes player.
     *
     * @param target     - target player.
     * @param banner     - banner.
     * @param reason     - mute reason.
     * @param parameters - additional parameters (can be null).
     */
    void mute(Actor target, Actor banner, String reason, PunishParameters parameters);

    /**
     * Temporary mutes player.
     *
     * @param target     - target player.
     * @param banner     - banner.
     * @param reason     - mute reason.
     * @param parameters - parameters
     * @param time       - expires.
     */
    void tempmute(Actor target, Actor banner, String reason, long time, PunishParameters parameters);

    /**
     * Unmutes player.
     *
     * @param actor      - target player.
     * @param parameters - parameters
     */
    void unmute(Actor actor, PunishParameters parameters);

    /**
     * Kicks player
     *
     * @param actor      - target player
     * @param reason     - kick reason
     * @param parameters - parameters
     */
    void kick(Actor actor, String reason, PunishParameters parameters);

    /**
     * Get player mute if present.
     *
     * @param target - target player
     * @return player mute or null if not exists.
     */
    Mute getMute(Actor target);

    /**
     * Get player ban if present.
     *
     * @param target - target player
     * @return player ban or null if not exists.
     */
    Ban getBan(Actor target);

    /**
     * Get all punishments (ban, tempban, mute, tempmute)
     *
     * @return collection of {@link Punishment}
     */
    Iterable<Punishment> getPunishments();

    /**
     * Get all bans.
     *
     * @return collection of {@link Ban}
     */
    Collection<Ban> getBans();

    /**
     * Get all mutes.
     *
     * @return collection of {@link Mute}
     */
    Collection<Mute> getMutes();

}
