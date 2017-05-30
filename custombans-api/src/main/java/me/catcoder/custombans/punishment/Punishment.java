package me.catcoder.custombans.punishment;

import lombok.*;
import me.catcoder.custombans.database.Database;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

/**
 * Created by Ruslan on 25.04.2017.
 */
@ToString
@RequiredArgsConstructor
@Getter
public abstract class Punishment {

    private final String reason;
    private final UUID uniqueId;
    private final String banner;
    @Getter(AccessLevel.PACKAGE)
    protected final String params;

    public abstract String getMessage();

    public boolean hasParameter(String key) {
        return params != null && Arrays.asList(params.split(",")).contains(key.toLowerCase());
    }

    public abstract void insertInto(Database database);
}
