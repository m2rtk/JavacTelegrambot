package bot;

import dao.Privacy;
import org.telegram.telegrambots.api.objects.Update;

import static dao.Privacy.CHAT;

public class Utils {

    /**
     * Surrounds input parameter with ``` and line.separator's
     * @param text input text.
     * @return surrounded text.
     */
    public static String toMonospace(String text) {
        return  "```" + System.getProperty("line.separator") + text + System.getProperty("line.separator") + "```";
    }

    /**
     * Gets id corresponding to privacy from update.
     * @param privacy privacy. CHAT or USER.
     * @param update  update to get id from.
     * @return chatId if CHAT, userId if USER
     */
    public static Long getId(Privacy privacy, Update update) {
        return privacy == CHAT ? update.getMessage().getChatId() : update.getMessage().getFrom().getId();
    }
}
