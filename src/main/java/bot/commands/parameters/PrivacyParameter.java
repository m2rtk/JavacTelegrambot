package bot.commands.parameters;

import bot.commands.Command;
import bot.commands.interfaces.NeedsPrivacy;
import bot.commands.visitors.Parameter;
import dao.Privacy;

public class PrivacyParameter extends Parameter {

    @Override
    public void visit(Command command) {
        if (command instanceof NeedsPrivacy) ((NeedsPrivacy) command).setPrivacy(Privacy.USER);
    }

    @Override
    public int hashCode() {
        return 1337;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PrivacyParameter;
    }
}
