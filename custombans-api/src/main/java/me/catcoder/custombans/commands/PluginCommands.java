package me.catcoder.custombans.commands;

import com.google.common.collect.Lists;
import com.sk89q.Command;
import com.sk89q.CommandContext;
import com.sk89q.CommandPermissions;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.ReloadIntent;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.punishment.Ban;
import me.catcoder.custombans.punishment.Mute;
import me.catcoder.custombans.punishment.TempBan;
import me.catcoder.custombans.punishment.TempMute;
import me.catcoder.custombans.utility.TimeUtility;

import java.util.List;

/**
 * @author CatCoder
 */
public class PluginCommands {

    private final CustomBans plugin;

    public PluginCommands(CustomBans plugin) {
        this.plugin = plugin;
    }


    @Command(aliases = {"custombans", "cb"},
            desc = "Информация."
    )
    public void custombans(CommandContext args, Actor actor) {
        actor.printMessage("&7[&cCustomBans&7]: v&c%s &7by &6CatCoder", plugin.getVersion());
    }

    @Command(aliases = {"cinfo", "finfo"},
            min = 1,
            max = 1,
            usage = "[player]",
            desc = "Информация о игроке")
    public void targetInfo(CommandContext args, Actor actor) {
        Actor target = plugin.getActor(args.getString(0));

        Ban ban = target.getBan();
        Mute mute = target.getMute();

        actor.printMessage("&6Информация о игроке: &a%s", target.getName() + "&6:");

        actor.printMessage("&6Забанен: %s", (ban == null ? "&cнет" : "&aда"));
        actor.printMessage("&6В муте: %s", (mute == null) ? "&cнет" : "&aда");
        if (ban != null) {
            actor.printMessage("   &6Причина: &a%s", ban.getReason());
            actor.printMessage("   &6Забанил: &a%s", ban.getBanner());
            if (ban instanceof TempBan) {
                actor.printMessage("   &6Осталось: &a%s", TimeUtility.getTime(((TempBan) ban).getExpires() - System.currentTimeMillis()));
            }
        }
        if (mute != null) {
            actor.printMessage("   &6Причина: &a%s", mute.getReason());
            actor.printMessage("   &6Выдал мут: &a%s", mute.getBanner());
            if (mute instanceof TempMute) {
                actor.printMessage("   &6Осталось: &a%s", TimeUtility.getTime(((TempMute) mute).getExpires() - System.currentTimeMillis()));
            }
        }
    }

    @Command(aliases = "cbreload",
            desc = "Перезагрузка плагина.",
            flags = "pcml")
    @CommandPermissions("custombans.reload")
    public void reload(CommandContext args, Actor actor) {
        ReloadIntent[] intents = parseIntents(args);

        for (ReloadIntent intent : intents) {
            plugin.reload(intent);
            actor.printMessage("&aReloaded: %s", intent);
        }
        actor.printMessage("&aOperation completed.");
    }

    private ReloadIntent[] parseIntents(CommandContext args) {
        if (args.getFlags().isEmpty()) return ReloadIntent.values();

        List<Character> flags = Lists.newArrayList(args.getFlags());
        ReloadIntent[] intents = new ReloadIntent[flags.size()];

        for (int i = 0; i < intents.length; i++) {
            char flag = flags.get(i);

            switch (flag) {
                case 'p':
                    intents[i] = ReloadIntent.PUNISHMENTS;
                    break;
                case 'c':
                    intents[i] = ReloadIntent.CONFIG;
                    break;
                case 'm':
                    intents[i] = ReloadIntent.LANGUAGE;
                    break;
                case 'l':
                    intents[i] = ReloadIntent.LIMITER;
                    break;
            }
        }
        return intents;
    }
}
