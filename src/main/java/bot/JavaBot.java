package bot;

import bot.commands.Command;
import bot.commands.JavaCommand;
import bot.commands.interfaces.NeedsPrivacy;
import bot.commands.parameters.PrivacyParameter;
import bot.commands.visitors.DAOVisitor;
import bot.commands.visitors.Parameter;
import bot.commands.visitors.StartTimeVisitor;
import dao.WriteToDiskBotDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import parser.CommandParser;
import parser.ParserException;
import parser.UnknownCommandException;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static dao.Privacy.CHAT;
import static dao.Privacy.USER;

public class JavaBot extends TelegramLongPollingBot {
    private static final Logger logger = LogManager.getLogger(JavaBot.class);

    private final DAOVisitor daoVisitor;
    private final StartTimeVisitor startTimeVisitor;
    public JavaBot() {
        this.daoVisitor       = new DAOVisitor(new WriteToDiskBotDAO());
        this.startTimeVisitor = new StartTimeVisitor(Instant.now().getEpochSecond());
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            logger.warn("IN: Update with no message " + update);
            return;
        }

        Thread.currentThread().setName(
                        update.getUpdateId() + "-" +
                        update.getMessage().getChatId() + "-" +
                        update.getMessage().getFrom().getUserName()
        );

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
                sendMessage(command.getOutput(), update);
            } catch (ParserException e) {
                sendMessage(Utils.toMonospace("Invalid command: " + e.getMessage()), update);
            }
        }
    }

    private Command getCommand(Update update) {
        Command command;
//        Map<String, Parameter> parameters;
        Set<Parameter> parameters;

        try {
            CommandParser parser = new CommandParser(update.getMessage().getText());
            parser.parse();

            command = parser.getCommand();
            parameters = parser.getParameters();
        } catch (UnknownCommandException e) {
            // let's assume that the command is a special case of java command
            command    = new JavaCommand();
            parameters = new HashSet<>();
            ((JavaCommand) command).setArgument(update.getMessage().getText().substring(1));
        }

        parameters.forEach(command::accept);
        command.accept(daoVisitor);
        command.accept(startTimeVisitor);

        if (command instanceof NeedsPrivacy) {
            if (((NeedsPrivacy)command).getPrivacy() == CHAT) ((NeedsPrivacy)command).setId(update.getMessage().getChatId());
            else ((NeedsPrivacy)command).setId(new Long(update.getMessage().getFrom().getId()));
        }

        return command;
    }

    public void sendMessage(String message, Update update) {
        for (String line : message.split(System.getProperty("line.separator"))) logger.info("OUT: '" + line + "'");

        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText(message); // TODO: 10.06.2017 probably should escape markdown
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("TelegramApiException", e);
        }
    }

    @Override
    public String getBotUsername() {
        return Config.JAVABOT_USER;
    }

    @Override
    public String getBotToken() {
        return Config.JAVABOT_TOKEN;
    }
}
