package bot.commands.parameters;

import bot.Commands;
import bot.commands.Command;
import bot.commands.interfaces.Private;

import static dao.Privacy.USER;

public class PrivacyParameter extends Parameter {
    private final long userId;


    public PrivacyParameter(long userId) {
        this.userId = userId;
    }

    @Override
    public void visit(Command command) {
        if (command instanceof Private) ((Private) command).setPrivacy(USER, userId);
    }

    @Override
    public String getName() {
        return Commands.privacyParam;
    }
}
