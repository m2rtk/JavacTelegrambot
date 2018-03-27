package com.github.m2rtk.telegram.bot.commands.interfaces;

import com.github.m2rtk.telegram.bot.commands.Command;

public interface CommandVisitor {

    /**
     * Visits command.
     * @param command command to visit.
     */
    void visit(Command command);
}
