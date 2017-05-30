package me.catcoder.custombans.commands;

import com.google.common.collect.Lists;
import com.sk89q.Command;
import com.sk89q.CommandContext;
import com.sk89q.CommandPermissions;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.ReloadIntent;
import me.catcoder.custombans.actor.Actor;

import java.util.List;
import java.util.Map;

/**
 * @author CatCoder
 */
public class PluginCommands {

    private final CustomBans plugin;

    public PluginCommands(CustomBans plugin) {
        this.plugin = plugin;
    }


    @Command(aliases = "cbreload",
            desc = "Reload the plugin.",
            usage = "[flags]",
            flags = "pcl"
    )
    @CommandPermissions("custombans.reload")
    public void reload(CommandContext args, Actor actor) {
        ReloadIntent[] intents = parseIntents(args);

        for (ReloadIntent intent : intents) {
            plugin.reload(intent);
            actor.printMessage("§aReloaded: %s", intent);
        }
        actor.printMessage("§aOperation completed successfully.");
    }

    @Command(aliases = {"custombans", "cb"},
            desc = "Plugin info command."
    )
    public void custombans(CommandContext args, Actor actor) {
        actor.printMessage("§7CustomBans v§c%s §7by §6CatCoder.", plugin.getVersion());
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
                case 'l':
                    intents[i] = ReloadIntent.LANGUAGE;
                    break;
            }
        }
        return intents;
    }
}
