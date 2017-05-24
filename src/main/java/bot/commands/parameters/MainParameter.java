package bot.commands.parameters;

import bot.Commands;
import bot.commands.interfaces.Argument;
import bot.commands.Command;
import bot.commands.JavacCommand;

/**
 * Works only on javac
 */
public class MainParameter extends Parameter implements Argument {
    private String classname;


    @Override
    public void visit(Command command) {
        if (command instanceof JavacCommand) {
            ((JavacCommand) command).wrapContentInMain(classname);
        }
    }

    @Override
    public String getName() {
        return Commands.mainParam;
    }


    @Override
    public void setArgument(String argument) {
        this.classname = argument;
    }

    public MainParameter set(String arg) {
        setArgument(arg);
        return this;
    }
}
