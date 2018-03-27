package com.github.m2rtk.telegram.bot.commands;

import com.github.m2rtk.telegram.bot.commands.interfaces.CommandVisitor;

public abstract class Command {
    private String output;

    /**
     * Accepts visitor parameter.
     * @param visitor visitor.
     */
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Execute command.
     * @throws IllegalExecutionException if the object doesn't have all the necessary data initialized.
     */
    public abstract void execute();

    /**
     * Returns output after calling execute();
     * Currently nice and help command don't need execute() call to return output.
     * @return output as string.
     * @throws NullPointerException if execute must have been called before calling this method.
     */
    public String getOutput() {
        if (output == null) throw new NullPointerException("Output is null, void execute() must be called before this method.");
        return output;
    }

    /**
     * Set output of command.
     * @param output new output.
     */
    protected void setOutput(String output) {
        this.output = output;
    }

    /**
     * Returns name of command.
     * @return Command name.
     */
    public abstract String getName();

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        else if (!(obj instanceof Command)) return false;
        return ((Command) obj).output == null && this.output == null || ((Command) obj).output.equals(this.output);
    }

    @Override
    public int hashCode() {
        int result = 11;
        return result * 31 + (this.output == null ? 0 : this.output.hashCode());
    }
}
