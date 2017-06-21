package bot.commands;

import bot.commands.interfaces.NeedsArgument;
import bot.commands.interfaces.NeedsDAO;
import bot.commands.interfaces.NeedsPrivacy;
import dao.BotDAO;
import dao.Privacy;

// // FIXME: 21.06.2017 asdNeedsPrivacy is bad here
public class KillCommand extends Command implements NeedsDAO, NeedsArgument, NeedsPrivacy {
    private BotDAO dao;
    private Integer pid;
    private Long chatId;

    @Override
    public void execute() {
        dao.getJavaProcess(pid, chatId).kill();
        setOutput("Killed " + pid);
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
    public void setPrivacy(Privacy privacy) {

    }

    @Override
    public void setId(Long id) {
        this.chatId = id;
    }

    @Override
    public Privacy getPrivacy() {
        return null;
    }

    @Override
    public String toString() {
        return "KillCommand{" +
                "dao=" + dao +
                ", pid=" + pid +
                ", chatId=" + chatId +
                "} " + super.toString();
    }
}
