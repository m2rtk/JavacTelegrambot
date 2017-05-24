package bot.commands;

import bot.Commands;
import bot.commands.interfaces.*;
import dao.BotDAO;
import dao.Privacy;
import dao.WriteToDiskBotDAO;
import javac.Compiled;

import java.util.Arrays;

import static dao.Privacy.CHAT;

public class JavaCommand implements Command, Argument, Private, NeedsDAO {
    private BotDAO dao;
    private String className;
    private String[] args;
    private Privacy privacy;
    private long id;

    private String output;

    public JavaCommand(long id) {
        this.privacy = CHAT;
        this.id = id;
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
    public void acceptParameter(Parameter parameterVisitor) {
        parameterVisitor.visit(this);
    }

    @Override
    public String getOutput() {
        return output;
    }

    @Override
    public String getName() {
        return Commands.java;
    }

    @Override
    public void setPrivacy(Privacy privacy, long id) {
        this.privacy = privacy;
        this.id = id;
    }

    @Override
    public void setArgument(String argument) {
        String[] pieces = argument.split(" ");
        this.className = pieces[0];
        this.args = new String[pieces.length - 1];
        System.arraycopy(pieces, 1, this.args, 0, pieces.length - 1);
    }

    @Override
    public void setDAO(BotDAO dao) {
        this.dao = dao;
    }

    @Override
    public String toString() {
        return "JavaCommand{" +
                "className='" + className + '\'' +
                ", args=" + Arrays.toString(args) +
                ", privacy=" + privacy +
                ", id=" + id +
                ", output='" + output + '\'' +
                '}';
    }
}
