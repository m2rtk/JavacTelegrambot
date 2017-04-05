package bot;

import java.Code;
import java.Compiled;
import dao.BotDAO;
import dao.InMemoryBotDAO;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.util.*;
import java.util.regex.Pattern;

public class BotMcBotfaceBot extends TelegramLongPollingBot {
    private static final String TAG = "BOTMCBOTFACEBOT";

    // TODO: 04.04.2017 Add database or some other sort of persistence
    private static final BotDAO dao = new InMemoryBotDAO();

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

            if (commmand.startsWith(Commands.help)) onHelpCommand(update);
            else if (commmand.startsWith(Commands.nice)) onNiceCommand(update);
            else if (commmand.startsWith(Commands.javac))onJavacCommand(update); // ordering is important
            else if (commmand.startsWith(Commands.java)) onJavaCommand(update);
            else if (commmand.startsWith(Commands.list)) onListCommand(update);
            else if (commmand.startsWith(Commands.delete)) onDeleteCommand(update);
        }
    }

    private void onDeleteCommand(Update update) {
        Map<String, String> params = getParameters(update.getMessage().getText());
        BotDAO.Privacy privacy = BotDAO.Privacy.CHAT;
        Long id = update.getMessage().getChatId();
        if (params.containsKey(Commands.privacyParam)) {
            privacy = BotDAO.Privacy.USER;
            id = new Long(update.getMessage().getFrom().getId());
        }
    }

    private void onListCommand(Update update) {
        Map<String, String> params = getParameters(update.getMessage().getText());
        BotDAO.Privacy privacy = BotDAO.Privacy.CHAT;
        Long id = update.getMessage().getChatId();
        if (params.containsKey(Commands.privacyParam)) {
            privacy = BotDAO.Privacy.USER;
            id = new Long(update.getMessage().getFrom().getId());
        }

        StringBuilder sb = new StringBuilder();
        List<String> names = new ArrayList<>(dao.getAll(id, privacy));
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
            dao.add(code.getCompiled(), null, null);
            sendMessage("Succesfully compiled!", update.getMessage().getChatId());
        } else {
            sendMessage("Compilation failed " + System.getProperty("line.separator") + code.getOut(),
                    update.getMessage().getChatId()
            );
        }
    }

    private void onJavacCommand2(Update update) {

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

        if (dao.isEmpty(null, null)) sendMessage("No scripts in database.", update.getMessage().getChatId());
        else if (!dao.contains(pieces[1], null, null)){
            sendMessage("Database doesn't contain script named '" + pieces[1] + "'", update.getMessage().getChatId());
        } else {
            compiledCode = dao.get(pieces[1], null, null);
            compiledCode.run(args);
            sendMessage(compiledCode.getOut(), update.getMessage().getChatId());

        }
    }

    private void onNiceCommand(Update update) {
        sendMessage("nice", update.getMessage().getChatId());
    }

    private void onHelpCommand(Update update) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello, I'm Javac Bot! I can compile and execute java code for you.").append("\n");
        sb.append("Use these commands to control me:").append("\n").append("\n");
        sb.append("/javac - compile java code.").append("\n");
        sb.append("Example:").append("\n");
        sb.append("'/javac public class HelloWorld {").append("\n");
        sb.append("                  public static void main(String[] args) {").append("\n");
        sb.append("                      System.out.println(\"Hello World!\");").append("\n");
        sb.append("                  }").append("\n");
        sb.append("              }'").append("\n");
        sb.append("Use '/javac -Classname to write only to main method").append("\n");
        sb.append("Example:").append("\n");
        sb.append("'/javac -HelloWorld System.out.println(\"Hello World!\");'").append("\n");
        sb.append("Both examples produce equivalent bytecode").append("\n").append("\n");
        sb.append("/java - execute compiled java code.").append("\n");
        sb.append("Example:").append("\n");
        sb.append("'/java HelloWorld' should output \"Hello World!\"");
        sendMessage(sb.toString(), update.getMessage().getChatId());
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

    /**
     * Returns a map of <Param, Argument>
     *     Currently assuming that every param only has 0 to 1 arguments.
     */
    private static Map<String, String> getParameters(String text) {
        Map<String, String> result = new HashMap<>();
        String[] pieces = text.split(" ");
        // [0] -> command Ex. /java
        // [i] -> parameter Ex. -p
        // [i + 1] -> parameter argument where applicable Ex. -c Test

        String param;
        for (int i = 1; i < pieces.length; i++) {
            if (pieces[i].charAt(0) == '-') {
                param = pieces[i].substring(1);
                if (pieces.length > i + 1 && pieces[i+1].charAt(0) != '-') {
                    result.put(param, pieces[i+1]);
                    i++;
                }
                else result.put(param, null);
            } else break;
        }

        return result;
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
