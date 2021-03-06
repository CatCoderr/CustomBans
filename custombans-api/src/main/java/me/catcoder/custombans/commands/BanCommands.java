package me.catcoder.custombans.commands;

import com.sk89q.*;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.language.MessageFormatter;
import me.catcoder.custombans.limit.LimitInfo;
import me.catcoder.custombans.limit.cooldown.Cooldowned;
import me.catcoder.custombans.punishment.ActionType;
import me.catcoder.custombans.punishment.PunishParameters;
import me.catcoder.custombans.utility.ParameterKeys;
import me.catcoder.custombans.utility.TimeUtility;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static me.catcoder.custombans.utility.ParameterKeys.*;

/**
 * @author CatCoder
 */
public class BanCommands {


    private final CustomBans plugin;

    public BanCommands(CustomBans plugin) {
        this.plugin = plugin;
    }

    @Command(aliases = {"ban", "cban"},
            usage = "[player] [reason]",
            flags = "cs",
            desc = "Забанить игрока пермаментно.",
            min = 1)
    @LimitedCommand(type = ActionType.BAN)
    @Cooldowned(type = ActionType.BAN)
    @CommandPermissions("custombans.ban")
    public void ban(CommandContext args, Actor actor) throws CommandException {
        LimitInfo info = args.getLocals().get(LimitInfo.class);

        Actor target = plugin.getActor(args.getString(0));

        if (!target.isOnline() && !actor.hasPermission("custombans.ban.online")) {
            throw new CommandException(MessageFormatter.create()
                    .addVariable("name", target.getName())
                    .format("limit.restricted_target_ban_online"));
        }

        if (target.getBan() != null) {
            throw new CommandException(MessageFormatter.create()
                    .addVariable("name", target.getName())
                    .format("errors.already_banned"));
        }

        String reason = MessageFormatter.create().format("no_reason");
        PunishParameters.PunishParametersBuilder builder = PunishParameters.builder();

        if (args.argsLength() > 1) {
            reason = args.getJoinedStrings(1);
        }

        if (!info.canBan(target)) {
            throw new CommandException(MessageFormatter.create()
                    .addVariable("target", target.getName())
                    .format("limit.restricted_target_ban"));
        }
        if (args.hasFlag('c')) {
            builder.append(CONSOLE_ACTION, true);
        }
        if (args.hasFlag('s')) {
            builder.append(SILENT, true);
        }
        plugin.getBanManager().ban(target, actor, reason, builder.build());
    }

    @Command(
            aliases = {"tempban", "ctempban"},
            usage = "[player] [time] [reason]",
            flags = "cs",
            desc = "Временно забанить игрока.",
            min = 2
    )
    @LimitedCommand(type = ActionType.TEMPBAN)
    @Cooldowned(type = ActionType.TEMPBAN)
    @CommandPermissions("custombans.tempban")
    public void tempban(CommandContext args, Actor actor) throws CommandException {
        LimitInfo info = args.getLocals().get(LimitInfo.class);

        Actor target = plugin.getActor(args.getString(0));

        if (!target.isOnline() && !actor.hasPermission("custombans.tempban.online")) {
            throw new CommandException(MessageFormatter.create()
                    .addVariable("name", target.getName())
                    .format("limit.restricted_target_tempban_online"));
        }
        if (target.getBan() != null) throw new CommandException(MessageFormatter.create()
                .addVariable("name", target.getName())
                .format("errors.already_temp_banned"));

        String reason = MessageFormatter.create().format("no_reason");
        PunishParameters.PunishParametersBuilder builder = PunishParameters.builder();
        long time = TimeUtility.parseTime(args);

        if (args.argsLength() > 3) {
            reason = args.getJoinedStrings(3);
        }

        if (!info.canBan(target)) {
            throw new CommandException(MessageFormatter.create()
                    .addVariable("target", target.getName())
                    .format("limit.restricted_target_tempban"));
        }
        if (!info.canAccess(time)) {
            throw new CommandException(MessageFormatter.create()
                    .addVariable("allowed", TimeUtility.getTime(new Date(TimeUnit.MINUTES.toMillis(info.getAllowedTime()))))
                    .format("limit.restricted_time"));
        }
        if (args.hasFlag('c')) {
            builder.append(CONSOLE_ACTION, true);
        }
        if (args.hasFlag('s')) {
            builder.append(SILENT, true);
        }
        plugin.getBanManager().tempban(target, actor, reason, time, builder.build());
    }

    @Command(
            aliases = {"unban", "cunban"},
            usage = "[player]",
            flags = "s",
            desc = "Разбанить игрока.",
            min = 1
    )
    @Cooldowned(type = ActionType.UNBAN)
    @CommandPermissions("custombans.unban")
    public void unban(CommandContext args, Actor actor) throws CommandException {
        Actor target = plugin.getActor(args.getString(0));

        if (target.getBan() == null) throw new CommandException(MessageFormatter.create()
                .addVariable("name", target.getName())
                .format("errors.ban_not_found"));

        if (target.getBan().hasParameter(ParameterKeys.CONSOLE_ACTION)
                && !target.hasPermission("custombans.unban.console"))
            throw new CommandException(MessageFormatter.create()
                    .format("errors.console_banned"));

        PunishParameters.PunishParametersBuilder builder = PunishParameters.builder();

        if (args.hasFlag('s')) {
            builder.append(ParameterKeys.SILENT, true);
        }
        builder.append(ParameterKeys.ACTOR, actor.getName());
        plugin.getBanManager().unban(target, builder.build());
    }
}
