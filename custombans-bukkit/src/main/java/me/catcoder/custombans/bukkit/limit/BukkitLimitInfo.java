package me.catcoder.custombans.bukkit.limit;

import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.bukkit.BukkitActor;
import me.catcoder.custombans.bukkit.BukkitLimiter;
import me.catcoder.custombans.bukkit.utility.PexUtility;
import me.catcoder.custombans.config.Configuration;
import me.catcoder.custombans.limit.LimitInfo;
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

    public BukkitLimitInfo(ActionType type, PermissionGroup group, BukkitLimiter limiter) {
        this.group = group;
        this.limitConfig = limiter.getLimitConfig();
        this.type = type;
    }


    @Override
    public boolean canAccess(long time) {
        if (getAllowedTime() == 0) return true;
        return TimeUnit.MINUTES.toMillis(getAllowedTime()) >= time;
    }

    @Override
    public boolean canBan(Actor target) {
        PermissionGroup targetGroup = PexUtility.getGroup((BukkitActor) target);

        return targetGroup.isChildOf(group, true);
    }

    @Override
    public long getAllowedTime() {
        return limitConfig.getInt("time_limit." + type.name().toLowerCase() + "." + group.getName(), 0);
    }
}
