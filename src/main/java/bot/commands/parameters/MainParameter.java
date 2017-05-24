package bot.commands.parameters;

import bot.Commands;
import bot.commands.interfaces.Command;
import bot.commands.JavacCommand;
import bot.commands.interfaces.Parameter;

/**
 * Works only on javac
 */
public class MainParameter implements Parameter {
    private final String classname;

    public MainParameter(String classname) {
        this.classname = classname;
    }

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
    public int getNrOfParams() {
        return 1;
    }

}
