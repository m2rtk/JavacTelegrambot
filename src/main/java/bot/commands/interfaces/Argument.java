package bot.commands.interfaces;

public interface Argument {

    /**
     * Set argument for object.
     * @param argument new argument.
     */
    void setArgument(String argument);

    /**
     * @return true if has argument, false otherwise
     */
    boolean hasArgument();
}
