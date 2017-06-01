package me.catcoder.custombans.bukkit.utility;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.bukkit.BukkitActor;
import me.catcoder.custombans.bukkit.BukkitLimiter;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author CatCoder
 */
public class PexUtility {

    static {
        try {
            getGroups = PermissionUser.class.getMethod("getGroups");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot find the method 'getGroups'!");
        }
    }

    private static final Method getGroups;


    public static PermissionGroup getGroup(BukkitActor actor) {
        PermissionUser user = PermissionsEx.getUser(actor.getPlayer());

        try {
            return Iterables.getFirst(getGroups(user), null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private static Iterable<PermissionGroup> getGroups(PermissionUser user) throws InvocationTargetException, IllegalAccessException {
        ImmutableList.Builder<PermissionGroup> builder = ImmutableList.builder();

        if (getGroups.getReturnType() == PermissionGroup[].class) {
            builder.add((PermissionGroup[]) getGroups.invoke(user));
        } else {
            Set<PermissionGroup> set = (Set<PermissionGroup>) getGroups.invoke(user);
            builder.add(set.toArray(new PermissionGroup[set.size()]));
        }
        return builder.build();
    }


}
