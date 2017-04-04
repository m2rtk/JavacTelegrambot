package Bot;

import Java.Code;
import Java.Compiled;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created on 25.03.2017.
 */
public class BotMcBotfaceBot extends TelegramLongPollingBot {
    private static final String TAG = "BOTMCBOTFACEBOT";

    // TODO: 04.04.2017 Add database or some other sort of persistence
    private static Map<String, Compiled> scripts = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        BotLogger.info(TAG, "onUpdateReceived called");

        if (!update.hasMessage()) {
            BotLogger.info(TAG, "No message, Update end");
            return;
        } else {
            BotLogger.info(TAG, "Update from chat: " + update.getMessage().getChatId());
        }

        if (update.getMessage().isCommand()) {
            String commmand = update.getMessage().getText().split(" ", 2)[0];

            if (commmand.startsWith(Commands.decide)) onDecideCommand(update);
            else if (commmand.startsWith(Commands.roll)) onRollCommand(update);
            else if (commmand.startsWith(Commands.help)) onHelpCommand(update);
            else if (commmand.startsWith(Commands.nice)) onNiceCommand(update);
            else if (commmand.startsWith(Commands.javac))onJavacCommand(update); // ordering is important
            else if (commmand.startsWith(Commands.java)) onJavaCommand(update);
            else if (commmand.startsWith(Commands.list)) onListCommand(update);
        }
    }

    private void onListCommand(Update update) {
        StringBuilder sb = new StringBuilder();
        List<String> names = new ArrayList<>(scripts.keySet());
        Collections.sort(names);
        for (String name : names) sb.append(name).append(System.getProperty("line.separator"));
        sendMessage(sb.toString(), update.getMessage().getChatId());
    }

    private void onJavacCommand(Update update) {
        String content = update.getMessage().getText();
        String name;
        String[] pieces = content.split(" ", 3);
        if (pieces.length > 2 && pieces[1].startsWith("-")) {
            name = pieces[1].substring(1);
            StringBuilder sb = new StringBuilder();
            sb.append("public class ").append(name).append(" {");
            sb.append(" public static void main(String[] args) {");
            sb.append(pieces[2]);
            sb.append("}}");
            content = sb.toString();
        } else {
            content = update.getMessage().getText().substring(7);
        }
        Code code = new Code(content);

        if (code.compile()) {
            scripts.put(code.getName(), code.getCompiled());
            sendMessage("Succesfully compiled!", update.getMessage().getChatId());
        } else {
            sendMessage("Compilation failed " + System.getProperty("line.separator") + code.getOut(),
                    update.getMessage().getChatId()
            );
        }
    }

    private void onJavaCommand(Update update) {
        Compiled compiledCode;
        String[] args;
        String[] pieces = update.getMessage().getText().split(" ");
        // 0    -> /java
        // 1    -> classname
        // 2... -> args

        if (pieces.length < 2) {
            sendMessage("Invalid input, no arguments.", update.getMessage().getChatId());
            return;
        }
        if (pieces.length > 2)
            args = Arrays.copyOfRange(pieces, 2, pieces.length);
        else
            args = new String[0];

        if (scripts.isEmpty()) sendMessage("No scripts in database.", update.getMessage().getChatId());
        else if (!scripts.containsKey(pieces[1])){
            sendMessage("Database doesn't contain script named '" + pieces[1] + "'", update.getMessage().getChatId());
        } else {
            compiledCode = scripts.get(pieces[1]);
            compiledCode.run(args);
            sendMessage(compiledCode.getOut(), update.getMessage().getChatId());

        }
    }

    private void onNiceCommand(Update update) {
        sendMessage("nice", update.getMessage().getChatId());
    }

    private void onHelpCommand(Update update) {
        sendMessage("Ask Märt", update.getMessage().getChatId());
    }

    // TODO: 04.04.2017 Remove this method
    private void onRollCommand(Update update) {
        String[] parts = update.getMessage().getText().substring(6).split("-");
        try {
            int left = Integer.parseInt(parts[0]);
            int right = Integer.parseInt(parts[1]);
            sendMessage(String.valueOf(randInt(Math.min(left, right), Math.max(left, right))), update.getMessage().getChatId());
        } catch (Exception e) {
            sendMessage("Invalid query, try again", update.getMessage().getChatId());
        }
    }

    private static int randInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    // TODO: 04.04.2017 Remove this method
    private void onDecideCommand(Update update) {
        Random rand = new Random();
        char c = update.getMessage().getText().charAt(7);
        String delimiter = " or ";
        if (c != ' ') delimiter = String.valueOf(c);
        String query = update.getMessage().getText().substring(8);
        String[] pieces = query.split(Pattern.quote(delimiter));
        sendMessage(pieces[rand.nextInt(pieces.length)], update.getMessage().getChatId());
    }


    private void sendMessage(String message, Long chatId) {
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
        return Config.BOTMCBOTFACEBOT_USER;
    }

    @Override
    public String getBotToken() {
        return Config.BOTMCBOTFACEBOT_TOKEN;
    }
}
