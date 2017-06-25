package bot;

import bot.commands.visitors.DAOVisitor;
import bot.commands.visitors.StartTimeVisitor;
import dao.BotDAO;
import dao.WriteToDiskBotDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.time.Instant;

public class JavaBot extends TelegramLongPollingBot {
    private static final Logger logger = LogManager.getLogger(JavaBot.class);
    private final DAOVisitor daoVisitor;
    private final StartTimeVisitor startTimeVisitor;

    public JavaBot(BotDAO dao) {
        this.daoVisitor = new DAOVisitor(dao);
        this.startTimeVisitor = new StartTimeVisitor(Instant.now().getEpochSecond());
    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.info("Launching new thread for update: " + update.getUpdateId());
        Thread thread = new UpdateHandler(update, this);
        thread.setUncaughtExceptionHandler((t, e) -> {
            logger.fatal(e);
            for (StackTraceElement ste : e.getStackTrace()) logger.fatal("\t\t" + ste);
        });
        thread.start();
    }

    public DAOVisitor getDaoVisitor() {
        return daoVisitor;
    }

    public StartTimeVisitor getStartTimeVisitor() {
        return startTimeVisitor;
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
