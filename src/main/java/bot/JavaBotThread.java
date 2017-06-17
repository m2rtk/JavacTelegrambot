package bot;

import bot.commands.Command;
import bot.commands.JavaCommand;
import bot.commands.interfaces.NeedsPrivacy;
import bot.commands.visitors.DAOVisitor;
import bot.commands.visitors.Parameter;
import bot.commands.visitors.StartTimeVisitor;
import dao.WriteToDiskBotDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import parser.CommandParser;
import parser.ParserException;
import parser.SpecialJavaCommandException;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static dao.Privacy.CHAT;

public class JavaBotThread extends Thread {
    private static final Logger logger = LogManager.getLogger(JavaBotThread.class);
    private static final DAOVisitor daoVisitor = new DAOVisitor(new WriteToDiskBotDAO());
    private static final StartTimeVisitor startTimeVisitor = new StartTimeVisitor(Instant.now().getEpochSecond());
    private static JavaBot bot;
    private final Update update;

    JavaBotThread(Update update) {
        this.setName(update.getUpdateId() + "-" +
                     update.getMessage().getChatId() + "-" +
                     update.getMessage().getFrom().getUserName()
        );

        this.update = update;
    }

    public static void setBot(JavaBot javaBot) {
        if (bot != null) throw new RuntimeException("Bot is already set.");
        bot = javaBot;
    }

    @Override
    public void run() {
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
                sendMessage(command.getOutput(), update.getMessage().getChatId());
            } catch (ParserException e) {
                sendMessage(Utils.toMonospace("Parser exception: " + e.getMessage()), update.getMessage().getChatId());
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

    private void sendMessage(String message, Long chatId) {
        for (String line : message.split(System.getProperty("line.separator"))) logger.info("OUT: '" + line + "'");

        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(chatId);
            sendMessage.setText(message);
            bot.sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("TelegramApiException", e);
        }
    }
}
