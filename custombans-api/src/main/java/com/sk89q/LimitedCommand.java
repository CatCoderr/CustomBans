package com.sk89q;

import me.catcoder.custombans.punishment.ActionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by CatCoder on 28.05.2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LimitedCommand {

    ActionType type();


}
