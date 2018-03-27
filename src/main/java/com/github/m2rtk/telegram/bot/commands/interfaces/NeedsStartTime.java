package com.github.m2rtk.telegram.bot.commands.interfaces;

public interface NeedsStartTime {

    /**
     * Set start time for object. In this case, the only object which needs start time is UpCommand.
     * @param startTime new start time.
     */
    void setStartTime(Long startTime);
}
