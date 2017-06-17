package bot.commands.interfaces;

import dao.Privacy;

public interface NeedsPrivacy {

    /**
     * Set privacy of implementing object.
     * @param privacy new privacy.
     *
     */
    void setPrivacy(Privacy privacy);

    /**
     * Set id of implementing object.
     * @param id new id corresponding to new privacy (chat id for CHAT and user id for USER).
     */
    void setId(Long id);

    Privacy getPrivacy();
}
