package me.catcoder.custombans.command;

import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.command.utility.CommandBase;
import me.catcoder.custombans.command.utility.CommandLocals;
import me.catcoder.custombans.punishment.ActionType;
import me.catcoder.custombans.utility.StringUtility;

import java.util.Deque;

/**
 * Created by CatCoder on 28.05.2017.
 */
public class LimitedCommand extends CommandBase {

    private final CommandBase reference;
    private final ActionType type;


    public LimitedCommand(CommandBase reference,
                          ActionType type) {
        super(reference.getAliases(),
                reference.getName(),
                reference.getPermission(),
                reference.getUsage());
        this.reference = reference;
        this.type = type;
    }

    @Override
    public boolean execute(Actor actor, Deque<String> args, final CommandLocals locals) {
        if (args.isEmpty()) {
            actor.printMessage(getUsage());
            return true;
        }

        String last = args.peekLast();

        if (!StringUtility.isInt(last) && type.isTemporary()) {
            actor.printMessage("%s is not a number.", last);
            return true;
        }
        int time = Integer.parseInt(last);

        //Check the limit
        CustomBans.getInstance().getLimiter().checkLimit(actor, type, time, locals);

        return reference.execute(actor, args, locals);
    }
}
