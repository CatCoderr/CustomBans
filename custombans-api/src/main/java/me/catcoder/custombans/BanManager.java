package me.catcoder.custombans;

import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.punishment.*;
import org.jetbrains.annotations.Nullable;

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
     * @param parameters - parameters.
     */
    void ban(Actor target, Actor banner, String reason, @Nullable PunishParameters parameters);

    /**
     * Unbans player.
     *
     * @param actor      - target player.
     * @param parameters - parameters
     */
    void unban(Actor actor, @Nullable PunishParameters parameters);

    /**
     * Permanently mutes player.
     *
     * @param target     - target player.
     * @param banner     - banner.
     * @param reason     - mute reason.
     * @param parameters - parameters.
     */
    void mute(Actor target, Actor banner, String reason, @Nullable PunishParameters parameters);

    /**
     * Unmutes player.
     *
     * @param actor      - target player.
     * @param parameters - parameters
     */
    void unmute(Actor actor, @Nullable PunishParameters parameters);

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
    Collection<Punishment> getPunishments();

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

    /**
     * Universal operation to clear player punishment. (database operation)
     *
     * @param type   - type
     * @param target - player
     * @return true if operation complete or false if failed.
     */
    boolean clear(ActionType type, Actor target);

}
