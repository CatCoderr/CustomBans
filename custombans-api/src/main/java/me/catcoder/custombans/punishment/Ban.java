package me.catcoder.custombans.punishment;

import java.util.UUID;

/**
 * Created by Ruslan on 25.04.2017.
 */
public class Ban extends Punishment {

    public Ban(String reason, UUID uniqueId, String banner) {
        super(reason, uniqueId, banner);
    }
}
