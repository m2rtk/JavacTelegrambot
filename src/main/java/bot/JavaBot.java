package bot;

import bot.commands.*;
import bot.commands.interfaces.Argument;
import bot.commands.interfaces.Command;
import bot.commands.interfaces.NeedsDAO;
import bot.commands.parameters.MainParameter;
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
import parser.data.ParameterToken;

import java.time.Instant;
import java.util.Map;

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

        Map<String, ParameterToken> parameters = parser.getParameters();
        long chatId = update.getMessage().getChatId();

        Command command = null;

        switch (parser.getCommand().getValue()) {
            case Commands.help:
                command = new HelpCommand();
                break;
            case Commands.nice:
                command = new NiceCommand();
                break;
            case Commands.up:
                command = new UpCommand(startTime);
                break;
            case Commands.list:
                command = new ListCommand(chatId);
                break;
            case Commands.delete:
                command = new DeleteCommand(chatId);
                break;
            case Commands.java:
                command = new JavaCommand(chatId);
                break;
            case Commands.javac:
                command = new JavacCommand(chatId);
                break;
        }

        assert command != null;

        if (command instanceof NeedsDAO)
            ((NeedsDAO) command).setDAO(dao);


        if (command instanceof Argument)
            ((Argument) command).setArgument(parser.getCommand().getArgument());


        if (parameters.containsKey(Commands.privacyParam))
            command.acceptParameter(new PrivacyParameter(update.getMessage().getFrom().getId()));


        if (parameters.containsKey(Commands.mainParam))
            command.acceptParameter(new MainParameter(parameters.get(Commands.mainParam).getArgument()));


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
