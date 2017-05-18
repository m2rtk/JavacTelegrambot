package bot.commands;

import dao.BotDAO;
import javac.Code;

public class JavacCommand implements Command {
    private final String content;
    private final BotDAO.Privacy privacy;
    private final Long id;
    private final BotDAO dao;

    private String output;

    public JavacCommand(String argument, String name, BotDAO.Privacy privacy, Long id, BotDAO dao) {
        if (name == null)
            this.content = argument;
        else
            this.content = String.format("public class %s { public static void main(String[] args) {%s}}", name, argument);

        this.privacy = privacy;
        this.id = id;
        this.dao = dao;
    }


    @Override
    public void execute() {
        Code code = new Code(content, privacy, id);

        if (code.compile()) {
            if (dao.get(code.getName(), id, privacy) != null) {
                dao.remove(code.getName(), id, privacy);
            }
            dao.add(code.getCompiled(), id, privacy);
            output = "Successfully compiled!";
        } else {
            output = "Compilation failed " + System.getProperty("line.separator") + code.getOut();
        }
    }

    @Override
    public String getOutput() {
        return output;
    }

    @Override
    public String getName() {
        return "javac";
    }
}
