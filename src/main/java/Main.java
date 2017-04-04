import Bot.BotMcBotfaceBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

// TODO: 04.04.2017 Write tests
public class Main {
    private static final String TAG = "MAIN";

    public static void main(String[] args) throws Exception{
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(new BotMcBotfaceBot());
        } catch (TelegramApiException e) {
            BotLogger.error(TAG, e);
        }
    }

}
