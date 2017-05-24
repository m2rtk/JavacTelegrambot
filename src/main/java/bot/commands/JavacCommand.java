package bot.commands;

import bot.Commands;
import bot.commands.interfaces.*;
import dao.BotDAO;
import dao.Privacy;
import javac.Code;

import static dao.Privacy.CHAT;

public class JavacCommand extends Command implements Argument, Private, NeedsDAO {
    private BotDAO dao;
    private String content;
    private Privacy privacy;
    private Long id;

    public JavacCommand(long id) {
        this.privacy = CHAT;
        this.id = id;
    }

    public void wrapContentInMain(String classname) {
        if (content == null) throw new NullPointerException("Content/argument must be set before calling this method.");
        this.content = String.format("public class %s { public static void main(String[] args) {%s}}", classname, content);
    }

    @Override
    public void execute() {
        if (content == null || id == null || privacy == null || dao == null) throw new IllegalExecutionException();
        Code code = new Code(content, privacy, id);

        if (code.compile()) {
            if (dao.get(code.getName(), id, privacy) != null) {
                dao.remove(code.getName(), id, privacy);
            }
            dao.add(code.getCompiled(), id, privacy);
            setOutput("Successfully compiled!");
        } else {
            setOutput("Compilation failed " + System.getProperty("line.separator") + code.getOut());
        }
    }

    @Override
    public String getName() {
        return Commands.javac;
    }

    @Override
    public void setPrivacy(Privacy privacy, long id) {
        this.privacy = privacy;
        this.id = id;
    }

    @Override
    public void setArgument(String argument) {
        this.content = argument;
    }

    @Override
    public void setDAO(BotDAO dao) {
        this.dao = dao;
    }

    @Override
    public String toString() {
        return "JavacCommand{" +
                "dao=" + dao +
                ", content='" + content + '\'' +
                ", privacy=" + privacy +
                ", id=" + id +
                "} " + super.toString();
    }
}
