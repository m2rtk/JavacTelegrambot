package bot.commands.interfaces;

import dao.Privacy;

public interface NeedsPrivacy {

    /**
     * Set privacy of implementing object.
     * @param privacy new privacy.
     *
     */
    void setPrivacy(Privacy privacy);

    Privacy getPrivacy();
}
