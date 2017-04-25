package me.catcoder.custombans.punishment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

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

}
