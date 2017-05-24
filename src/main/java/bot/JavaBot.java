package bot;

import bot.commands.*;
import dao.BotDAO;
import dao.WriteToDiskBotDAO;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import parser.CommandParser;
import parser.ParserException;
import parser.data.ParameterToken;
import parser.data.Token;

import java.time.Instant;
import java.util.*;

import static dao.BotDAO.Privacy;
import static dao.BotDAO.Privacy.CHAT;
import static dao.BotDAO.Privacy.USER;

public class JavaBot extends TelegramLongPollingBot {
    private static final String TAG = "JAVABOT";

    // Non-final for testing purposes.
    private static BotDAO dao = new WriteToDiskBotDAO();

    private final long startTime;
    public JavaBot() {
        startTime = Instant.now().getEpochSecond();
        new DirectInputThread(this).start();
    }

    @Override
    public void onUpdateReceived(Update update) {
        BotLogger.info(TAG, "Update received!");

        if (!update.hasMessage()) {
            BotLogger.info(TAG, "No message, Update end");
            return;
        }

        Long chat = update.getMessage().getChatId();

        if (update.getMessage().isCommand()) {
            BotLogger.info(TAG, "Update from chat: " + update.getMessage().getChatId()
                                         + " user: " + update.getMessage().getFrom().getId()
                                         + " content: " + System.getProperty("line.separator")
                                         + update.getMessage().getText());

            try {
                Command command = getCommand(update);
                command.execute();
                BotLogger.info(TAG, "Executed command " + command.getName()
                        + " in chat " + chat
                        + " with output " + command.getOutput()
                );
                sendMessage(command.getOutput(), chat);
            } catch (ParserException e) {
                sendMessage("Invalid command: " + e.getMessage(), chat);
            }
        }
    }

    private Command getCommand(Update update) {
        String s =  update.getMessage().getText().split(" ")[0];
        if (s.length() > 1 && Character.isUpperCase(s.charAt(1))) {
            return new JavaCommand(s.substring(1), Privacy.CHAT, update.getMessage().getChatId(), dao);
        }
        CommandParser parser = new CommandParser(update.getMessage().getText());
        parser.parse();
        String command = parser.getCommand().getValue();


        if (command.equals(Commands.help))  return new HelpCommand();
        if (command.equals(Commands.nice))  return new NiceCommand();
        if (command.equals(Commands.up))    return new UpCommand(startTime);

        // the following commands make use of parameters
        Map<String, ParameterToken> parameters = parser.getParameters();

        Privacy privacy = parameters.containsKey(Commands.privacyParam) ? USER : CHAT;
        Long id = privacy == CHAT ? update.getMessage().getChatId() : new Long(update.getMessage().getFrom().getId());

        if (command.equals(Commands.list))   return new ListCommand(privacy, id, dao);

        // the following commands take arguments
        String argument = parser.getCommand().getArgument();

        if (command.equals(Commands.delete)) return new DeleteCommand(argument, privacy, id, dao);
        if (command.equals(Commands.java))   return new JavaCommand(argument, privacy, id, dao);

        // javac can make use of -m parameter
        String name = null;
        if (parameters.containsKey(Commands.mainParam)) name = parameters.get(Commands.mainParam).getArgument();

        if (command.equals(Commands.javac))  return new JavacCommand(argument, name, privacy, id, dao);


        throw new RuntimeException();
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
