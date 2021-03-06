package com.github.m2rtk.telegram.bot.commands.parameters;

import com.github.m2rtk.telegram.bot.Commands;
import com.github.m2rtk.telegram.bot.commands.Command;
import com.github.m2rtk.telegram.bot.commands.JavacCommand;
import com.github.m2rtk.telegram.bot.commands.interfaces.NeedsArgument;
import com.github.m2rtk.telegram.bot.commands.visitors.Parameter;

/**
 * Works only on com.github.m2rtk.telegram.javac
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MainParameter that = (MainParameter) o;

        return classname != null ? classname.equals(that.classname) : that.classname == null;
    }

    @Override
    public int hashCode() {
        return classname != null ? classname.hashCode() : 0;
    }
}
