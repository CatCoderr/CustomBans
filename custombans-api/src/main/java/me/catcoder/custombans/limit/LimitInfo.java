package me.catcoder.custombans.limit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.catcoder.custombans.punishment.ActionType;

/**
 * Created by CatCoder on 28.05.2017.
 */
@AllArgsConstructor
@Getter
public class LimitInfo {

    private Result result;
    private String disallowMessage;
    private ActionType type;

    public boolean allowed() {
        return result == Result.ALLOWED;
    }

    public static enum Result {
        ALLOWED, DISALLOWED
    }
}
