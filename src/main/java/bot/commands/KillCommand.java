package bot.commands;

import bot.commands.interfaces.NeedsArgument;
import bot.commands.interfaces.NeedsDAO;
import bot.commands.interfaces.NeedsPrivacy;
import bot.commands.interfaces.NeedsUpdate;
import dao.BotDAO;
import dao.Privacy;
import org.telegram.telegrambots.api.objects.Update;

public class KillCommand extends Command implements NeedsDAO, NeedsArgument, NeedsUpdate {
    private BotDAO dao;
    private Integer pid;
    private Update update;

    @Override
    public void execute() {
        dao.getJavaProcess(pid, update.getMessage().getChatId()).kill();
        setOutput("Killed " + pid + ".");
    }

    @Override
    public void setDAO(BotDAO dao) {
        this.dao = dao;
    }

    @Override
    public void setArgument(String argument) {
        this.pid = Integer.parseInt(argument);
    }

    @Override
    public boolean hasArgument() {
        return pid != null;
    }


    @Override
    public void setUpdate(Update update) {
        this.update = update;
    }

    @Override
    public String toString() {
        return "KillCommand{" +
                "dao=" + dao +
                ", pid=" + pid +
                ", updateId=" + update.getUpdateId() +
                "} " + super.toString();
    }
}
