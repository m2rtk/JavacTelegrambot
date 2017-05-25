package bot.commands.interfaces;

import dao.Privacy;

public interface Private {

    /**
     * Set privacy and corresponding id of implementing object.
     * @param privacy new privacy.
     * @param id new id corresponding to new privacy (chat id for CHAT and user id for USER).
     */
    void setPrivacy(Privacy privacy, Long id);

}
