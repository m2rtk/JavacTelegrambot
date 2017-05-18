package bot.commands;

import org.telegram.telegrambots.api.objects.Update;

import static dao.BotDAO.Privacy;

/**
 * Created on 18.05.2017.
 */
public class ListCommand implements Command {
    private final Privacy privacy;

    public ListCommand(Update update) {
        this.privacy = Privacy.CHAT;
    }

    @Override
    public void execute() {

    }

    @Override
    public String getOutput() {
        return null;
    }
}
