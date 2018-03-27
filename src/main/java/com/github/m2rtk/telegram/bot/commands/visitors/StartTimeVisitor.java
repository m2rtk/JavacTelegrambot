package com.github.m2rtk.telegram.bot.commands.visitors;

import com.github.m2rtk.telegram.bot.commands.Command;
import com.github.m2rtk.telegram.bot.commands.interfaces.CommandVisitor;
import com.github.m2rtk.telegram.bot.commands.interfaces.NeedsStartTime;

public class StartTimeVisitor implements CommandVisitor{
    private final long startTime;

    public StartTimeVisitor(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public void visit(Command command) {
        if (command instanceof NeedsStartTime) ((NeedsStartTime) command).setStartTime(this.startTime);
    }
}
