package bot.commands;

import bot.Commands;
import bot.commands.interfaces.*;
import dao.BotDAO;
import dao.Privacy;

import static dao.Privacy.CHAT;


public class DeleteCommand extends Command implements Private, Argument, NeedsDAO {
    private BotDAO dao;
    private String argument;
    private Privacy privacy;
    private Long id;

    public DeleteCommand(Long chatId) {
        this.privacy = CHAT;
        this.id = chatId;
    }

    @Override
    public void execute() {
        if (argument == null || id == null || privacy == null || dao == null) throw new IllegalExecutionException();
        boolean successful = dao.remove(argument, id, privacy);

        if (successful) setOutput("Successfully deleted " + argument);
        else            setOutput("Couldn't delete " + argument);
    }

    @Override
    public String getName() {
        return Commands.delete;
    }

    @Override
    public void setPrivacy(Privacy privacy, long id) {
        this.privacy = privacy;
        this.id = id;
    }

    @Override
    public void setArgument(String argument) {
        this.argument = argument;
    }

    @Override
    public void setDAO(BotDAO dao) {
        this.dao = dao;
    }

    @Override
    public String toString() {
        return "DeleteCommand{" +
                "dao=" + dao +
                ", argument='" + argument + '\'' +
                ", privacy=" + privacy +
                ", id=" + id +
                "} " + super.toString();
    }
}
