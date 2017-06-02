package me.catcoder.custombans.bukkit;

import lombok.RequiredArgsConstructor;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.punishment.Ban;
import me.catcoder.custombans.punishment.Mute;
import me.catcoder.custombans.punishment.TempBan;
import me.catcoder.custombans.punishment.TempMute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * @author CatCoder
 */
@RequiredArgsConstructor
public class BukkitListener implements Listener {


    private final CustomBans plugin;


    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Actor actor = plugin.getActor(event.getPlayer().getName());
        Ban ban = actor.getBan();

        if (ban == null) return;

        event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
        event.setKickMessage(ban.getMessage());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Actor actor = plugin.getActor(event.getPlayer().getName());
        Mute mute = actor.getMute();

        if (mute == null) return;

        event.setCancelled(true);
        actor.printMessage(mute.getMessage());
    }
}
