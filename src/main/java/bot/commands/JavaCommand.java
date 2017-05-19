package bot.commands;

import bot.Commands;
import dao.BotDAO;
import javac.Compiled;

public class JavaCommand implements Command {
    private final String className;
    private final String[] args;
    private final BotDAO.Privacy privacy;
    private final Long id;
    private final BotDAO dao;

    private String output;

    public JavaCommand(String argument, BotDAO.Privacy privacy, Long id, BotDAO dao) {
        String[] pieces = argument.split(" ");
        this.className = pieces[0];
        this.args = new String[pieces.length - 1];
        System.arraycopy(pieces, 1, this.args, 0, pieces.length - 1);
        this.privacy = privacy;
        this.id = id;
        this.dao = dao;
    }

    @Override
    public void execute() {
        Compiled compiled = dao.get(className, id, privacy);

        if (compiled == null) {
            output = "Database doesn't contain script named '" + className + "'";
            return;
        }

        compiled.run(args);

        output = compiled.getOut();
    }

    @Override
    public String getOutput() {
        return output;
    }

    @Override
    public String getName() {
        return Commands.java;
    }
}
