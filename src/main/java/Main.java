import bot.JavaBot;
import bot.JavaBotThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        logger.info("Bot starting!");
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            JavaBot bot = new JavaBot();
            JavaBotThread.setBot(bot);
            botsApi.registerBot(bot);
            logger.info("Bot started!");
        } catch (TelegramApiException e) {
            logger.error("shit", e);
        }
    }
}
