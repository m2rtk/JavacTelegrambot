package com.github.m2rtk.telegram.bot.commands.interfaces;

import com.github.m2rtk.telegram.dao.BotDAO;

public interface NeedsDAO {

    /**
     * Set com.github.m2rtk.telegram.dao for object.
     * @param dao new com.github.m2rtk.telegram.dao.
     */
    void setDAO(BotDAO dao);
}
