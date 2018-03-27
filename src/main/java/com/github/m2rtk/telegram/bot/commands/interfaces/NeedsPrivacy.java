package com.github.m2rtk.telegram.bot.commands.interfaces;

import com.github.m2rtk.telegram.dao.Privacy;

public interface NeedsPrivacy {

    /**
     * Set privacy and corresponding id of implementing object.
     * @param privacy new privacy.
     * @param id new id corresponding to new privacy (chat id for CHAT and user id for USER).
     */
    void setPrivacy(Privacy privacy, Long id);

}
