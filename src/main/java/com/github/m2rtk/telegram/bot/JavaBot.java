package com.github.m2rtk.telegram.bot;

import com.github.m2rtk.telegram.bot.commands.Command;
import com.github.m2rtk.telegram.bot.commands.JavaCommand;
import com.github.m2rtk.telegram.bot.commands.parameters.PrivacyParameter;
import com.github.m2rtk.telegram.bot.commands.visitors.DAOVisitor;
import com.github.m2rtk.telegram.bot.commands.visitors.Parameter;
import com.github.m2rtk.telegram.bot.commands.visitors.StartTimeVisitor;
import com.github.m2rtk.telegram.dao.WriteToDiskBotDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import com.github.m2rtk.telegram.parser.CommandParser;
import com.github.m2rtk.telegram.parser.ParserException;
import com.github.m2rtk.telegram.parser.UnknownCommandException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.github.m2rtk.telegram.dao.Privacy.CHAT;
import static com.github.m2rtk.telegram.dao.Privacy.USER;

public class JavaBot extends TelegramLongPollingBot {
    private static final Logger log = LogManager.getLogger();

    private final String token;
    private final String username;

    private final DAOVisitor daoVisitor;
    private final StartTimeVisitor startTimeVisitor;

    public JavaBot(String token, String username) {
        this.token = token;
        this.username = username;
        this.daoVisitor       = new DAOVisitor(new WriteToDiskBotDAO());
        this.startTimeVisitor = new StartTimeVisitor(Instant.now().getEpochSecond());
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Update received!");

        if (!update.hasMessage()) {
            log.info("No message, Update end");
            return;
        }

        long chatId = update.getMessage().getChatId();

        if (update.getMessage().isCommand()) {
            log.info("Update from chat: " + update.getMessage().getChatId()
                                         + " user: " + update.getMessage().getFrom().getId()
                                         + " content: " + System.getProperty("line.separator")
                                         + update.getMessage().getText());

            try {
                Command command = getCommand(update);
                command.execute();
                log.info("Executed command " + command.getName()
                        + " in chat " + chatId
                        + " with output " + System.getProperty("line.separator")
                        + command.getOutput()
                );
                sendMessage(command.getOutput(), chatId);
            } catch (ParserException e) {
                sendMessage("Invalid command: " + e.getMessage(), chatId);
            }
        }
    }

    private Command getCommand(Update update) {
        Command command;
        Map<String, Parameter> parameters;

        try {
            CommandParser parser = new CommandParser(update.getMessage().getText());
            parser.parse();

            command = parser.getCommand();
            parameters = parser.getParameters();
        } catch (UnknownCommandException e) {
            command    = new JavaCommand();
            parameters = new HashMap<>();
            ((JavaCommand) command).setArgument(update.getMessage().getText().substring(1));
        }

        setPrivacy(parameters, update);
        parameters.values().forEach(command::accept);
        command.accept(daoVisitor);
        command.accept(startTimeVisitor);
        return command;
    }

    // I don't like this solution todo fix
    private void setPrivacy(Map<String, Parameter> parameters, Update update) {
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();

        if (parameters.containsKey(Commands.privacyParameter))
            ((PrivacyParameter) parameters.get(Commands.privacyParameter)).setPrivacy(USER, userId);
        else
            parameters.put(Commands.privacyParameter, new PrivacyParameter().set(CHAT, chatId));
    }

    public void sendMessage(String message, Long chatId) {
        log.info("Sending message \n" + message + "\nto chat: " + chatId);
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(chatId);
            sendMessage.setText("```\n" + message + "```"); // TODO: 10.06.2017 probably should escape markdown
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
