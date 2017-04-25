package me.catcoder.custombans.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Ruslan on 25.04.2017.
 */
@Getter
@RequiredArgsConstructor
public class CommandPermissionException extends Exception{

    private final CommandBase command;
}
