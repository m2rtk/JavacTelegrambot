package bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class JavaBot extends TelegramLongPollingBot {
    private static final Logger logger = LogManager.getLogger(JavaBot.class);

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

    @Override
    public String getBotUsername() {
        return Config.JAVABOT_USER;
    }

    @Override
    public String getBotToken() {
        return Config.JAVABOT_TOKEN;
    }
}
