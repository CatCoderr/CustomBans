package me.catcoder.custombans.bukkit;

import com.sk89q.CommandLocals;
import com.sk89q.Console;
import lombok.Getter;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.bukkit.limit.BukkitLimitInfo;
import me.catcoder.custombans.bukkit.utility.PexUtility;
import me.catcoder.custombans.config.Configuration;
import me.catcoder.custombans.limit.LimitInfo;
import me.catcoder.custombans.limit.Limiter;
import me.catcoder.custombans.punishment.ActionType;
import me.catcoder.custombans.utility.ConfigUtility;

import java.io.File;

/**
 * @author CatCoder
 */
public class BukkitLimiter implements Limiter {

    @Getter
    private Configuration limitConfig;
    private CustomBans customBans;

    public BukkitLimiter(CustomBans customBans) {
        this.customBans = customBans;
        this.reload();
    }

    @Override
    public void checkLimit(Actor sender, ActionType type, CommandLocals locals) {
        if (sender instanceof ConsoleActor) {
            locals.put(LimitInfo.class, ConsoleActor.GOD);
            return;
        }
        locals.put(LimitInfo.class, new BukkitLimitInfo(type, PexUtility.getGroup((BukkitActor) sender), this));
    }

    @Override
    public void reload() {
        this.limitConfig = ConfigUtility.get(customBans.getConfigurationLoader(), new File(customBans.getWorkingDirectory(), "limits.yml"));
    }


}
