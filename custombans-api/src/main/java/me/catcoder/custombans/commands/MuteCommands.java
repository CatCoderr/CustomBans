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

import static me.catcoder.custombans.utility.ParameterKeys.*;

/**
 * @author CatCoder
 */
public class MuteCommands {

    private final CustomBans plugin;

    public MuteCommands(CustomBans plugin) {
        this.plugin = plugin;
    }


    @Command(aliases = {"mute", "cmute"},
            usage = "[player] [reason]",
            flags = "cs",
            desc = "Выдать мут игроку пермаментно.",
            min = 1)
    @LimitedCommand(type = ActionType.MUTE)
    @Cooldowned(type = ActionType.MUTE)
    @CommandPermissions("custombans.mute")
    public void mute(CommandContext args, Actor actor) throws CommandException {
        LimitInfo info = args.getLocals().get(LimitInfo.class);
        Actor target = plugin.getActor(args.getString(0));

        if (!target.isOnline() && !actor.hasPermission("custombans.mute.online")) {
            throw new CommandException(MessageFormatter.create()
                    .addVariable("name", target.getName())
                    .format("limit.restricted_target_mute_online"));
        }

        if (target.getMute() != null) throw new CommandException(MessageFormatter.create()
                .addVariable("name", target.getName())
                .format("errors.already_muted"));

        String reason = MessageFormatter.create().format("no_reason");
        PunishParameters.PunishParametersBuilder builder = PunishParameters.builder();

        if (args.argsLength() > 1) {
            reason = args.getJoinedStrings(1);
        }

        if (!info.canBan(target)) {
            throw new CommandException(MessageFormatter.create()
                    .addVariable("target", target.getName())
                    .format("limit.restricted_target_mute"));
        }
        if (args.hasFlag('c')) {
            builder.append(CONSOLE_ACTION, true);
        }
        if (args.hasFlag('s')) {
            builder.append(SILENT, true);
        }
        plugin.getBanManager().mute(target, actor, reason, builder.build());
    }

    @Command(
            aliases = {"tempmute", "ctempmute"},
            usage = "[player] [time] [reason]",
            flags = "cs",
            desc = "Выдать мут на время игроку.",
            min = 2
    )
    @LimitedCommand(type = ActionType.TEMPMUTE)
    @Cooldowned(type = ActionType.TEMPMUTE)
    @CommandPermissions("custombans.tempmute")
    public void tempmute(CommandContext args, Actor actor) throws CommandException {
        LimitInfo info = args.getLocals().get(LimitInfo.class);

        Actor target = plugin.getActor(args.getString(0));

        if (!target.isOnline() && !actor.hasPermission("custombans.tempmute.online")) {
            throw new CommandException(MessageFormatter.create()
                    .addVariable("name", target.getName())
                    .format("limit.restricted_target_tempmute_online"));
        }

        if (target.getMute() != null) throw new CommandException(MessageFormatter.create()
                .addVariable("name", target.getName())
                .format("errors.already_temp_muted"));

        String reason = MessageFormatter.create().format("no_reason");
        PunishParameters.PunishParametersBuilder builder = PunishParameters.builder();
        long time = TimeUtility.parseTime(args);

        if (args.argsLength() > 3) {
            reason = args.getJoinedStrings(3);
        }

        if (!info.canBan(target)) {
            throw new CommandException(MessageFormatter.create()
                    .addVariable("target", target.getName())
                    .format("limit.restricted_target_tempmute"));
        }
        if (!info.canAccess(time)) {
            throw new CommandException(MessageFormatter.create()
                    .addVariable("allowed", TimeUtility.getTime(new Date(info.getAllowedTime())))
                    .format("limit.restricted_time"));
        }
        if (args.hasFlag('c')) {
            builder.append(CONSOLE_ACTION, true);
        }
        if (args.hasFlag('s')) {
            builder.append(SILENT, true);
        }
        plugin.getBanManager().tempmute(target, actor, reason, time, builder.build());
    }

    @Command(
            aliases = {"unmute", "cunmute"},
            usage = "[player]",
            flags = "s",
            desc = "Забрать мут у игрока.",
            min = 1
    )
    @Cooldowned(type = ActionType.UNMUTE)
    @CommandPermissions("custombans.unmute")
    public void unmute(CommandContext args, Actor actor) throws CommandException {
        Actor target = plugin.getActor(args.getString(0));

        if (target.getMute() == null) throw new CommandException(MessageFormatter.create()
                .addVariable("name", target.getName())
                .format("errors.mute_not_found"));

        if (target.getMute().hasParameter(ParameterKeys.CONSOLE_ACTION) && !target.hasPermission("custombans.mute.console")) {
            throw new CommandException(MessageFormatter.create()
                    .format("errors.console_muted"));
        }

        PunishParameters.PunishParametersBuilder builder = PunishParameters.builder();

        if (args.hasFlag('s')) {
            builder.append(ParameterKeys.SILENT, true);
        }
        builder.append(ParameterKeys.ACTOR, actor.getName());
        plugin.getBanManager().unmute(target, builder.build());
    }
}
