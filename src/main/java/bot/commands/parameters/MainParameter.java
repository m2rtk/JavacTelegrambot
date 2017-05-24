package bot.commands.parameters;

import bot.Commands;
import bot.commands.interfaces.Argument;
import bot.commands.interfaces.Command;
import bot.commands.JavacCommand;
import bot.commands.interfaces.Parameter;

/**
 * Works only on javac
 */
public class MainParameter implements Parameter, Argument {
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
