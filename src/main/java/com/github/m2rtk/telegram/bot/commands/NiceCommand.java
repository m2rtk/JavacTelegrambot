package com.github.m2rtk.telegram.bot.commands;

import com.github.m2rtk.telegram.bot.Commands;

public class NiceCommand extends Command {

    @Override
    public void execute() {
        setOutput("nice");
    }

    @Override
    public String getName() {
        return Commands.nice;
    }

    @Override
    public String toString() {
        return "NiceCommand{}";
    }
}
