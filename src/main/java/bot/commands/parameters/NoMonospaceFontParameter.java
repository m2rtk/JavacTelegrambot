package bot.commands.parameters;

import bot.commands.Command;

public class NoMonospaceFontParameter extends Parameter {

    @Override
    public void visit(Command command) {
        command.setMonospaceFont(false);
    }

    @Override
    public int hashCode() {
        return 13337;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NoMonospaceFontParameter;
    }
}
