package me.catcoder.custombans.bukkit;

import com.google.common.base.Preconditions;
import com.sk89q.*;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.Platform;
import me.catcoder.custombans.ReloadIntent;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.bukkit.command.CommandsManagerRegistration;
import me.catcoder.custombans.commands.BanCommands;
import me.catcoder.custombans.commands.MuteCommands;
import me.catcoder.custombans.commands.PluginCommands;
import me.catcoder.custombans.language.MessageFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * CustomBans implementation for Bukkit.
 *
 * @author CatCoder
 */
public class CustomBansBukkit extends JavaPlugin {


    /**
     * Called when plugin is enabled.
     */

    private CustomBans customBans;
    private CommandsManagerRegistration commands;

    @Override
    public void onEnable() {
        try {
            //Create plugins/CustomBans folder
            getDataFolder().mkdir();
            //Implementing API.
            customBans = CustomBans.builder()
                    .logger(getLogger())
                    .platform(Platform.BUKKIT)
                    .version(getDescription().getVersion())
                    .workingDirectory(getDataFolder())
                    .actorFunction(this::getActor)
                    .reloader(this::reload)
                    .build();
            //Set limiter and ban manager
            customBans.setLimiter(new BukkitLimiter(customBans));
            customBans.setBanManager(new BukkitBanManager(customBans));
            //Load punishments.
            reload(ReloadIntent.PUNISHMENTS);
            //Registering commands to Bukkit command map
            this.commands = new CommandsManagerRegistration(this, customBans.getCommandExecutor());
            this.registerCommands();
        } catch (IOException | SQLException e) {
            getLogger().log(Level.SEVERE, "Cannot setting up CustomBans.", e);
        }
    }

    @Override
    public void onDisable() {
        //Unregister commands.
        commands.unregisterCommands();
    }

    /**
     * Commands handling
     *
     * @param sender  - sender
     * @param command - unique command
     * @param label   - label
     * @param args    - arguments /test 1 2 3
     * @return true if command completed successfully.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Actor actor = (sender instanceof Player) ? new BukkitActor((Player) sender) : ConsoleActor.INSTANCE;

        try {
            customBans.getCommandExecutor().execute(command.getName(), args, actor, actor);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(MessageFormatter.create().format("errors.no_permissions"));
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(MessageFormatter.create().format("errors.invalid_number_format"));
            } else {
                sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
        return true;
    }

    private void registerCommands() {
        commands.register(PluginCommands.class);
        commands.register(BanCommands.class);
        commands.register(MuteCommands.class);
    }


    private Actor getActor(String name) {
        Player player = Bukkit.getPlayerExact(name);
        return player == null ? new BukkitActor(name) : new BukkitActor(player);
    }

    private void reload(ReloadIntent intent) {
        switch (intent) {
            case CONFIG:
                customBans.reloadPluginConfiguration();
                break;
            case LANGUAGE:
                try {
                    customBans.reloadLanguage();
                } catch (IOException e) {
                    getLogger().log(Level.WARNING, "Cannot reload language file.", e);
                }
                break;
            case PUNISHMENTS:
                customBans.getStorage().load();
                break;
        }
    }
}
