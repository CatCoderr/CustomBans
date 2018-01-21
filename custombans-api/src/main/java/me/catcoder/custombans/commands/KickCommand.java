package me.catcoder.custombans.commands;

import com.sk89q.*;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.language.MessageFormatter;
import me.catcoder.custombans.limit.LimitInfo;
import me.catcoder.custombans.limit.cooldown.Cooldowned;
import me.catcoder.custombans.punishment.ActionType;
import me.catcoder.custombans.punishment.PunishParameters;

import static me.catcoder.custombans.utility.ParameterKeys.SILENT;

/**
 * @author CatCoder
 */
public class KickCommand {

    private final CustomBans plugin;

    public KickCommand(CustomBans plugin) {
        this.plugin = plugin;
    }


    @Command(
            aliases = {"kick", "ckick"},
            desc = "Кикнуть игрока.",
            min = 1,
            flags = "s",
            usage = "[player] [reason]"
    )
    @CommandPermissions("custombans.kick")
    @Cooldowned(type = ActionType.KICK)
    @LimitedCommand(type = ActionType.KICK)
    public void kick(CommandContext args, Actor actor) throws CommandException {
        LimitInfo info = args.getLocals().get(LimitInfo.class);

        Actor target = plugin.getActor(args.getString(0));
        if (!target.isOnline()) throw new CommandException(MessageFormatter.create().format("errors.target_offline"));

        String reason = MessageFormatter.create().format("no_reason");
        PunishParameters.PunishParametersBuilder builder = PunishParameters.builder();

        if (args.argsLength() > 1) {
            reason = args.getJoinedStrings(1);
        }

        if (!info.canBan(target)) {
            throw new CommandException(MessageFormatter.create()
                    .addVariable("target", target.getName())
                    .format("limit.restricted_target_kick"));
        }
        if (args.hasFlag('s')) {
            builder.append(SILENT, true);
        }
        plugin.getBanManager().kick(target, reason, builder.build());
    }
}
