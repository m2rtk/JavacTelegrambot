package bot;

import bot.commands.Command;
import bot.commands.interfaces.NeedsDAO;
import bot.commands.interfaces.StartTime;
import bot.commands.parameters.Parameter;
import bot.commands.parameters.PrivacyParameter;
import dao.BotDAO;
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
                System.out.println(command);
                command.execute();
                System.out.println(command);
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
        CommandParser parser = new CommandParser(update.getMessage().getText());
        parser.parse();

        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();

        Command command = parser.getCommand();
        Map<String, Parameter> parameters = parser.getParameters();

        if (parameters.containsKey(Commands.privacyParameter))
            ((PrivacyParameter) parameters.get(Commands.privacyParameter)).setPrivacy(USER, userId);
        else
            parameters.put(Commands.privacyParameter, new PrivacyParameter().set(CHAT, chatId));

        for (Parameter parameter : parameters.values()) command.acceptParameter(parameter);

        if (command instanceof NeedsDAO)  ((NeedsDAO)  command).setDAO(dao);
        if (command instanceof StartTime) ((StartTime) command).setStartTime(startTime);

        return command;
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
