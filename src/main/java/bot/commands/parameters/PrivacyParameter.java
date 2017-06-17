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
}
