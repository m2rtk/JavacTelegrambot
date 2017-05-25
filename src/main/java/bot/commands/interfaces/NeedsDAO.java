package bot.commands.interfaces;

import dao.BotDAO;

public interface NeedsDAO {

    /**
     * Set dao for object.
     * @param dao new dao.
     */
    void setDAO(BotDAO dao);
}
