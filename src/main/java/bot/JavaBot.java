package bot;

import bot.commands.visitors.Command;
import bot.commands.visitors.Parameter;
import bot.commands.parameters.PrivacyParameter;
import bot.commands.visitors.DAOVisitor;
import bot.commands.visitors.StartTimeVisitor;
import dao.WriteToDiskBotDAO;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import parser.CommandParser;
import parser.ParserException;

import java.time.Instant;
import java.util.Map;

import static dao.Privacy.CHAT;
import static dao.Privacy.USER;

public class JavaBot extends TelegramLongPollingBot {
    private static final String TAG = "JAVABOT";

    private final DAOVisitor daoVisitor;
    private final StartTimeVisitor startTimeVisitor;

    public JavaBot() {
        this.daoVisitor       = new DAOVisitor(new WriteToDiskBotDAO());
        this.startTimeVisitor = new StartTimeVisitor(Instant.now().getEpochSecond());
    }

    @Override
    public void onUpdateReceived(Update update) {
        BotLogger.info(TAG, "Update received!");

        if (!update.hasMessage()) {
            BotLogger.info(TAG, "No message, Update end");
            return;
        }

        long chatId = update.getMessage().getChatId();

        if (update.getMessage().isCommand()) {
            BotLogger.info(TAG, "Update from chat: " + update.getMessage().getChatId()
                                         + " user: " + update.getMessage().getFrom().getId()
                                         + " content: " + System.getProperty("line.separator")
                                         + update.getMessage().getText());

            try {
                Command command = getCommand(update);
                command.execute();
                BotLogger.info(TAG, "Executed command " + command.getName()
                        + " in chat " + chatId
                        + " with output " + command.getOutput()
                );
                sendMessage(command.getOutput(), chatId);
            } catch (ParserException e) {
                sendMessage("Invalid command: " + e.getMessage(), chatId);
            }
        }
    }

    private Command getCommand(Update update) {
        CommandParser parser = new CommandParser(update.getMessage().getText());
        parser.parse();

        Command command = parser.getCommand();
        Map<String, Parameter> parameters = parser.getParameters();
        setPrivacy(parameters, update);

        parameters.values().forEach(command::accept);
        command.accept(daoVisitor);
        command.accept(startTimeVisitor);

        return command;
    }

    // I don't like this solution
    private void setPrivacy(Map<String, Parameter> parameters, Update update) {
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();

        if (parameters.containsKey(Commands.privacyParameter))
            ((PrivacyParameter) parameters.get(Commands.privacyParameter)).setPrivacy(USER, userId);
        else
            parameters.put(Commands.privacyParameter, new PrivacyParameter().set(CHAT, chatId));
    }

    public void sendMessage(String message, Long chatId) {
        BotLogger.info(TAG, "Sending message \n" + message + "\nto chat: " + chatId);
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(message);
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            BotLogger.error(TAG, e);
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
