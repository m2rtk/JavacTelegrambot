package bot.commands.parameters;

import bot.Commands;
import bot.commands.interfaces.NeedsArgument;
import bot.commands.visitors.Command;
import bot.commands.JavacCommand;
import bot.commands.visitors.Parameter;

/**
 * Works only on javac
 */
public class MainParameter extends Parameter implements NeedsArgument {
    private String classname;


    @Override
    public void visit(Command command) {
        if (command instanceof JavacCommand) ((JavacCommand) command).wrapContentInMain(classname);
    }

    @Override
    public String getName() {
        return Commands.mainParameter;
    }


    @Override
    public void setArgument(String argument) {
        this.classname = argument;
    }

    @Override
    public boolean hasArgument() {
        return this.classname != null;
    }

    public MainParameter set(String arg) {
        setArgument(arg);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        else if (!(obj instanceof MainParameter)) return false;
        return (((MainParameter) obj).classname == null && this.classname == null)  || (((MainParameter) obj).classname.equals(this.classname));
    }

    @Override
    public int hashCode() {
        int result = 11;
        result = 31 * result + (this.classname == null ? 0 : this.classname.hashCode());
        return result;
    }
}
