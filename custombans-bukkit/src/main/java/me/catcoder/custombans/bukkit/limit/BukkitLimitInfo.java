package me.catcoder.custombans.bukkit.limit;

import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.bukkit.BukkitActor;
import me.catcoder.custombans.bukkit.BukkitLimiter;
import me.catcoder.custombans.bukkit.utility.PexUtility;
import me.catcoder.custombans.config.Configuration;
import me.catcoder.custombans.limit.LimitInfo;
import me.catcoder.custombans.limit.Limiter;
import me.catcoder.custombans.limit.cooldown.Cooldown;
import me.catcoder.custombans.punishment.ActionType;
import ru.tehkode.permissions.PermissionGroup;

import java.util.concurrent.TimeUnit;

/**
 * @author CatCoder
 */
public class BukkitLimitInfo implements LimitInfo {


    private final PermissionGroup group;
    private final Configuration limitConfig;
    private final ActionType type;
    private final Limiter limiter;
    private final int priority;

    public BukkitLimitInfo(ActionType type, PermissionGroup group, BukkitLimiter limiter, int priority) {
        this.group = group;
        this.limitConfig = limiter.getLimitConfig();
        this.limiter = limiter;
        this.type = type;
        this.priority = priority;
    }


    @Override
    public boolean canAccess(long time) {
        return getAllowedTime() == 0 || TimeUnit.MINUTES.toMillis(getAllowedTime()) >= time;
    }


    @Override
    public boolean canBan(Actor target) {
        int targetPriority = limiter.getCustomPriority(target);
        if (targetPriority != -1 && priority != -1 && targetPriority >= priority) return false;

        PermissionGroup targetGroup = PexUtility.getGroup((BukkitActor) target);

        return targetGroup.isChildOf(group, true);
    }

    @Override
    public long getAllowedTime() {
        return limitConfig.getInt("time_limit." + type.name().toLowerCase() + "." + group.getName(), 0);
    }
}
