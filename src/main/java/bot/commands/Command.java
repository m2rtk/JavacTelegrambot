package bot.commands;

import bot.Utils;
import bot.commands.interfaces.CommandVisitor;

public abstract class Command {
    private String output;
    private boolean usesMonospaceFont = true;

    /**
     * Accepts visitor parameter.
     * @param visitor visitor.
     */
    public final void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Execute command.
     * @throws IllegalExecutionException if the object doesn't have all the necessary data initialized.
     */
    public abstract void execute();

    /**
     * Yeh
     * @param usesMonospaceFont duh
     */
    public final void setMonospaceFont(boolean usesMonospaceFont) {
        this.usesMonospaceFont = usesMonospaceFont;
    }

    /**
     * Returns output after calling execute();
     * Currently nice and help command don't need execute() call to return output.
     * @return output as string.
     * @throws NullPointerException if execute must have been called before calling this method.
     */
    public final String getOutput() {
        if (output == null) throw new NullPointerException("Output is null, void execute() must be called before this method.");
        if (usesMonospaceFont) return Utils.toMonospace(output);
        return output;
    }

    /**
     * Set output of command.
     * @param output new output.
     */
    protected final void setOutput(String output) {
        this.output = output;
    }

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
