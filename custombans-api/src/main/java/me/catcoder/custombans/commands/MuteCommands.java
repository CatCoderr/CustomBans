package me.catcoder.custombans.commands;

import com.sk89q.*;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.language.MessageFormatter;
import me.catcoder.custombans.limit.LimitInfo;
import me.catcoder.custombans.punishment.ActionType;
import me.catcoder.custombans.punishment.PunishParameters;
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
            desc = "Mutes player permanently.",
            min = 1)
    @LimitedCommand(type = ActionType.MUTE)
    @CommandPermissions("custombans.mute")
    public void mute(CommandContext args, Actor actor) throws CommandException {
        LimitInfo info = args.getLocals().get(LimitInfo.class);
        Actor target = plugin.getActor(args.getString(0));
        String reason = MessageFormatter.create().format("errors.no_reason");
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
            desc = "Temporary mutes player.",
            min = 2
    )
    @LimitedCommand(type = ActionType.TEMPMUTE)
    @CommandPermissions("custombans.tempmute")
    public void tempban(CommandContext args, Actor actor) throws CommandException {
        LimitInfo info = args.getLocals().get(LimitInfo.class);

        Actor target = plugin.getActor(args.getString(0));
        String reason = MessageFormatter.create().format("messages.no_reason");
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
                    .addVariable("allowed", TimeUtility.getTime(new Date(info.getAllowedTime()), false))
                    .format("limit.restricted_time"));
        }
        if (args.hasFlag('c')) {
            builder.append(CONSOLE_ACTION, true);
        }
        if (args.hasFlag('s')) {
            builder.append(SILENT, true);
        }
        plugin.getBanManager().tempmute(target, actor, reason, System.currentTimeMillis() + time, builder.build());
    }
}
