package bot.commands;

import bot.Commands;
import dao.BotDAO;

public class DeleteCommand implements Command {
    private final String argument;
    private final BotDAO.Privacy privacy;
    private final Long id;
    private final BotDAO dao;

    private String output;

    public DeleteCommand(String argument, BotDAO.Privacy privacy, Long id, BotDAO dao) {
        this.argument = argument;
        this.privacy = privacy;
        this.id = id;
        this.dao = dao;
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
}
