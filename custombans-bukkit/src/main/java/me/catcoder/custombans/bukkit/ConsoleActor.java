package me.catcoder.custombans.bukkit;

import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.limit.LimitInfo;
import me.catcoder.custombans.punishment.Ban;
import me.catcoder.custombans.punishment.Mute;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author CatCoder
 */
public class ConsoleActor implements Actor {


    private static final CommandSender CONSOLE = Bukkit.getConsoleSender();

    public static final ConsoleActor INSTANCE = new ConsoleActor();
    public static final LimitInfo GOD = new LimitInfo() {
        @Override
        public boolean canAccess(long time) {
            return true;
        }

        @Override
        public boolean canBan(Actor target) {
            return true;
        }

        @Override
        public long getAllowedTime() {
            return 0;
        }
    };

    @Override
    public void printMessage(String message, Object... objects) {
        CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(message, objects)));
    }

    @Override
    public boolean hasPermission(String permission) {
        return CONSOLE.hasPermission(permission);
    }

    @Override
    public String getName() {
        return CONSOLE.getName();
    }

    @Override
    public boolean isOnline() {
        return true;
    }


    @Override
    public Ban getBan() {
        throw new UnsupportedOperationException("Player method only.");
    }

    @Override
    public Mute getMute() {
        throw new UnsupportedOperationException("Player method only.");
    }

}
