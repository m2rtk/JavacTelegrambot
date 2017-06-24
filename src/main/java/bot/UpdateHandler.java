package bot;

import bot.commands.Command;
import bot.commands.JavaCommand;
import bot.commands.parameters.Parameter;
import bot.commands.visitors.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import parser.CommandParser;
import parser.ParserException;
import parser.SpecialJavaCommandException;
import utils.Utils;

import java.util.HashSet;
import java.util.Set;

public class UpdateHandler extends Thread {
    private static final Logger logger = LogManager.getLogger(UpdateHandler.class);
    private final JavaBot bot;
    private final Update update;

    public UpdateHandler(Update update, JavaBot bot) {
        this.setName(update.getUpdateId() + "-" +
                     update.getMessage().getChatId() + "-" +
                     update.getMessage().getFrom().getUserName()
        );

        this.update = update;
        this.bot = bot;
    }

    @Override
    public void run() {
        if (!update.hasMessage()) {
            logger.warn("IN: Update with no message " + update);
            return;
        }

        logger.info("IN: update=(id=" + update.getUpdateId() +
                ", chat=" + update.getMessage().getChatId() +
                ", user=" + update.getMessage().getFrom().getId() + ")"
        );

        for (String line : update.getMessage().getText().split("\n")) logger.info("IN: " + " '" + line + "'");

        if (update.getMessage().isCommand()) {
            try {
                Command command = getCommand(update);
                logger.info("CMD: " + command);
                command.execute();
                sendMessage(command.getOutput());
            } catch (ParserException e) {
                sendMessage(Utils.toMonospace("Parser exception: " + e.getMessage()));
            }
        }
    }

    private Command getCommand(Update update) throws ParserException {
        Command command;
        Set<Parameter> parameters;

        try {
            CommandParser parser = new CommandParser(update.getMessage().getText());
            parser.parse();

            command = parser.getCommand();
            parameters = parser.getParameters();
        } catch (SpecialJavaCommandException e) {
            // the command is a special case of java command
            command    = new JavaCommand();
            parameters = new HashSet<>();
            String argument = update.getMessage().getText().substring(1); // we capitalize it
            ((JavaCommand) command).setArgument(argument.length() == 1 ? argument : argument.substring(0, 1).toUpperCase() + argument.substring(1));
        }

        parameters.forEach(command::accept);
        command.accept(bot.getDaoVisitor());
        command.accept(bot.getStartTimeVisitor());
        command.accept(new UpdateVisitor(update));
        command.accept(new UpdateHandlerVisitor(this));

        return command;
    }

    public void sendMessage(String message) {
        for (String line : message.split(System.getProperty("line.separator"))) logger.info("OUT: '" + line + "'");

        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText(message);
            bot.sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("TelegramApiException", e);
            for (StackTraceElement ste : e.getStackTrace()) logger.error("\t\t" + ste);
        }
    }

    public long getChat() {
        return update.getMessage().getChatId();
    }
}
