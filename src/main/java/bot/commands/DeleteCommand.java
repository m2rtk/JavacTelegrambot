package bot.commands;

import bot.Commands;
import bot.commands.interfaces.*;
import dao.BotDAO;
import dao.Privacy;
import dao.WriteToDiskBotDAO;

import static dao.Privacy.CHAT;


public class DeleteCommand extends Command implements Private, Argument, NeedsDAO {
    private BotDAO dao;
    private String argument;
    private Privacy privacy;
    private long id;

    private String output;

    public DeleteCommand(long chatId) {
        this.privacy = CHAT;
        this.id = chatId;
    }

    @Override
    public void execute() {
        boolean successful = dao.remove(argument, id, privacy);

        if (successful) output = "Successfully deleted " + argument;
        else output = "Couldn't delete " + argument;
    }

    @Override
    public String getOutput() {
        return output;
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
                "argument='" + argument + '\'' +
                ", privacy=" + privacy +
                ", id=" + id +
                ", output='" + output + '\'' +
                '}';
    }

}
