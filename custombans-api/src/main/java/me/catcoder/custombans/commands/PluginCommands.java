package me.catcoder.custombans.commands;

import com.google.common.collect.Lists;
import com.sk89q.Command;
import com.sk89q.CommandContext;
import com.sk89q.CommandPermissions;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.ReloadIntent;
import me.catcoder.custombans.actor.Actor;

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
