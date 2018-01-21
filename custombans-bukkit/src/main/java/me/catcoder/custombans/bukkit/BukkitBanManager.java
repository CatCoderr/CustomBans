package me.catcoder.custombans.bukkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import me.catcoder.custombans.api.BanManager;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.language.MessageFormatter;
import me.catcoder.custombans.punishment.*;
import me.catcoder.custombans.utility.ParameterKeys;
import me.catcoder.custombans.utility.TimeUtility;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.Date;

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
        String banParams = null;
        if (parameters != null && parameters.hasParameter(ParameterKeys.CONSOLE_ACTION)) {
            banParams = ParameterKeys.CONSOLE_ACTION;
        }
        Ban ban = new Ban(reason, target.getName(), banner.getName(), banParams);
        customBans.getStorage().add(ban);

        //Announce message
        announceMessage(MessageFormatter.create()
                .addVariable("banner", banner.getName())
                .addVariable("name", target.getName())
                .addVariable("reason", reason)
                .format("announcement.player_banned"), parameters != null && parameters.hasParameter(ParameterKeys.SILENT));
        //Kick player if he is online
        if (target.isOnline()) {
            ((BukkitActor) target).getPlayer().kickPlayer(ban.getMessage());
        }
    }

    @Override
    public void tempban(Actor target, Actor banner, String reason, long time, PunishParameters parameters) {
        String muteParams = null;
        if (parameters.hasParameter(ParameterKeys.CONSOLE_ACTION)) {
            muteParams = ParameterKeys.CONSOLE_ACTION;
        }
        TempBan tempBan = new TempBan(reason, target.getName(), banner.getName(), System.currentTimeMillis() + time, muteParams);
        customBans.getStorage().add(tempBan);
        //Announce message
        announceMessage(MessageFormatter.create()
                .addVariable("banner", banner.getName())
                .addVariable("name", target.getName())
                .addVariable("reason", reason)
                .addVariable("time", TimeUtility.getTime(new Date(time)))
                .format("announcement.player_temp_banned"), parameters.hasParameter(ParameterKeys.SILENT));
        //Kick player if he is online
        if (target.isOnline()) {
            ((BukkitActor) target).getPlayer().kickPlayer(tempBan.getMessage());
        }
    }

    @Override
    public void unban(Actor actor, PunishParameters parameters) {
        Preconditions.checkArgument(parameters != null, "Unban parameters cannot be null.");
        customBans.getStorage().removeBan(actor.getName());

        announceMessage(MessageFormatter.create()
                .addVariable("admin", parameters.get(ParameterKeys.ACTOR))
                .addVariable("name", actor.getName())
                .format("announcement.player_unbanned"), parameters.hasParameter(ParameterKeys.SILENT));
    }

    @Override
    public void mute(Actor target, Actor banner, String reason, PunishParameters parameters) {
        String muteParams = null;
        if (parameters != null && parameters.hasParameter(ParameterKeys.CONSOLE_ACTION)) {
            muteParams = ParameterKeys.CONSOLE_ACTION;
        }
        customBans.getStorage().add(new Mute(reason, target.getName(), banner.getName(), muteParams));

        //Announce message
        announceMessage(MessageFormatter.create()
                .addVariable("banner", banner.getName())
                .addVariable("name", target.getName())
                .addVariable("reason", reason)
                .format("announcement.player_muted"), parameters != null && parameters.hasParameter(ParameterKeys.SILENT));
    }

    @Override
    public void tempmute(Actor target, Actor banner, String reason, long time, PunishParameters parameters) {
        String muteParams = null;
        if (parameters != null && parameters.hasParameter(ParameterKeys.CONSOLE_ACTION)) {
            muteParams = ParameterKeys.CONSOLE_ACTION;
        }
        customBans.getStorage().add(new TempMute(reason, target.getName(), banner.getName(), System.currentTimeMillis() + time, muteParams));

        //Announce message
        announceMessage(MessageFormatter.create()
                .addVariable("banner", banner.getName())
                .addVariable("name", target.getName())
                .addVariable("reason", reason)
                .addVariable("time", TimeUtility.getTime(new Date(time)))
                .format("announcement.player_temp_muted"), parameters != null && parameters.hasParameter(ParameterKeys.SILENT));
    }

    @Override
    public void unmute(Actor actor, PunishParameters parameters) {
        Preconditions.checkArgument(parameters != null, "Unmute parameters cannot be null.");
        customBans.getStorage().removeMute(actor.getName());

        announceMessage(MessageFormatter.create()
                .addVariable("admin", parameters.get(ParameterKeys.ACTOR))
                .addVariable("name", actor.getName())
                .format("announcement.player_unmuted"), parameters.hasParameter(ParameterKeys.SILENT));
    }

    @Override
    public void kick(Actor actor, String reason, PunishParameters parameters) {
        Preconditions.checkArgument(parameters != null, "Kick parameters cannot be null.");
        ((BukkitActor) actor).getPlayer().kickPlayer(reason);
        announceMessage(MessageFormatter.create()
                .addVariable("name", actor.getName())
                .addVariable("kicker", parameters.get(ParameterKeys.ACTOR))
                .addVariable("reason", reason)
                .format("announcement.player_kicked"), parameters.hasParameter(ParameterKeys.SILENT));
    }

    @Override
    public Mute getMute(Actor target) {
        Mute mute = customBans.getStorage().getMutes().get(target.getName().toLowerCase());

        if (mute != null && mute instanceof TempMute && ((TempMute) mute).hasExpired()) {
            customBans.getStorage().removeMute(mute.getName());
            return null;
        }
        return mute;
    }

    @Override
    public Ban getBan(Actor target) {
        Ban ban = customBans.getStorage().getBans().get(target.getName().toLowerCase());

        if (ban != null && ban instanceof TempBan && ((TempBan) ban).hasExpired()) {
            customBans.getStorage().removeBan(ban.getName());
            return null;
        }
        return ban;
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

    private void announceMessage(String message, boolean silent) {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> !silent || player.hasPermission("custombans.viewsilent"))
                .forEach(player -> {
                    String prefix = (silent ? MessageFormatter.create().format("silent_prefix") : "");
                    player.sendMessage(prefix.concat(message));
                });
        Bukkit.getConsoleSender().sendMessage(message);
    }
}
