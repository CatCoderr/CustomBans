package me.catcoder.custombans.bukkit;

import com.google.common.base.Preconditions;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.Platform;
import me.catcoder.custombans.ReloadIntent;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.database.Database;
import org.bukkit.Bukkit;
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

    @Override
    public void onEnable() {
        try {
            //Implementing API.
            customBans = CustomBans.builder()
                    .logger(getLogger())
                    .platform(Platform.BUKKIT)
                    .version(getDescription().getVersion())
                    .workingDirectory(getDataFolder())
                    .actorFunction(this::getActor)
                    .reloader(this::reload)
                    // .limiter()
                    // .banManager()
                    .build();
        } catch (IOException | Database.ConnectionException | SQLException e) {
            getLogger().log(Level.SEVERE, "Cannot setting up CustomBans.", e);
        }
    }

    private Actor getActor(String name) {
        Player player = Bukkit.getPlayerExact(name);
        Preconditions.checkArgument(player != null, "Player %s is offline.", name);
        return new BukkitActor(player);
    }

    private void reload(ReloadIntent intent) {
        switch (intent) {
            case CONFIG:
                customBans.reloadPluginConfiguration();
                break;
            case LANGUAGE:
                try {
                    customBans.getLanguage().reload();
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
