package me.catcoder.custombans.bukkit;

import com.google.common.collect.Iterables;
import com.sk89q.CommandLocals;
import lombok.Getter;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.bukkit.limit.BukkitLimitInfo;
import me.catcoder.custombans.bukkit.utility.PexUtility;
import me.catcoder.custombans.config.Configuration;
import me.catcoder.custombans.limit.LimitInfo;
import me.catcoder.custombans.limit.Limiter;
import me.catcoder.custombans.limit.cooldown.Cooldown;
import me.catcoder.custombans.limit.cooldown.Cooldowns;
import me.catcoder.custombans.punishment.ActionType;
import me.catcoder.custombans.utility.FileUtility;
import ru.tehkode.permissions.PermissionGroup;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author CatCoder
 */
public class BukkitLimiter implements Limiter {

    @Getter
    private Configuration limitConfig;
    private final CustomBans customBans;

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
        PermissionGroup group = PexUtility.getGroup((BukkitActor) sender);
        locals.put(LimitInfo.class, new BukkitLimitInfo(
                type,
                group,
                this,
                getCustomPriority(sender)));
    }

    @Override
    public void checkCooldown(Actor sender, CommandLocals locals, ActionType type) {
        if (sender instanceof ConsoleActor) return; //Its god.
        locals.put(Cooldown.class, calculateCooldown(sender, PexUtility.getGroup((BukkitActor) sender), type, customBans, locals));
    }

    private static Cooldown calculateCooldown(Actor sender, PermissionGroup group, ActionType type, CustomBans customBans, CommandLocals locals) {
        Configuration cooldownSection = customBans.getPluginConfiguration().getSection("cooldowns");
        String uniqueKey = sender.getName() + "-" + type.toString();
        Cooldown cooldown = Cooldowns.getCooldown(uniqueKey);
        Runnable whenExecuted = new Runnable() {
            @Override
            public void run() {
                int cooldownTime = cooldownSection.getInt(group.getName());
                if (cooldownTime <= 0 && !sender.hasPermission("custombans.cooldown.bypass")) {
                    cooldownTime = cooldownSection.getInt(Iterables.getFirst(cooldownSection.getKeys(), "def"));
                }
                if (cooldownTime > 0) {
                    Cooldowns.setCooldown(uniqueKey, TimeUnit.SECONDS.toMillis(cooldownTime));
                }
            }
        };

        locals.put(Runnable.class, whenExecuted);

        return cooldown;
    }

    @Override
    public int getCustomPriority(Actor actor) {
        return customBans.getPluginConfiguration().getInt("priorities." + actor.getName(), -1);
    }

    @Override
    public void reload() {
        this.limitConfig = FileUtility.get(customBans.getConfigurationLoader(), new File(customBans.getWorkingDirectory(), "limits.yml"));
    }


}
