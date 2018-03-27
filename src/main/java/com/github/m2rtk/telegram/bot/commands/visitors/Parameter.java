package com.github.m2rtk.telegram.bot.commands.visitors;

import com.github.m2rtk.telegram.bot.commands.Command;
import com.github.m2rtk.telegram.bot.commands.interfaces.CommandVisitor;

public abstract class Parameter implements CommandVisitor {

    /**
     * Visits command.
     * @param command command to visit.
     */
    public abstract void visit(Command command);

    /**
     * Returns name of parameter.
     * @return Parameter name.
     */
    public abstract String getName();

}
