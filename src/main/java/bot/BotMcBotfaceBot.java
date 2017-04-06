package bot;

import dao.BotDAO;
import dao.WriteToDiskBotDAO;
import javac.Code;
import javac.Compiled;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.util.*;
import java.util.stream.Collectors;

public class BotMcBotfaceBot extends TelegramLongPollingBot {
    private static final String TAG = "BOTMCBOTFACEBOT";

    // TODO: 04.04.2017 Add database or some other sort of persistence
    private static final BotDAO dao = new WriteToDiskBotDAO();

    @Override
    public void onUpdateReceived(Update update) {
        BotLogger.info(TAG, "onUpdateReceived called");

        if (!update.hasMessage()) {
            BotLogger.info(TAG, "No message, Update end");
            return;
        } else {
            BotLogger.info(TAG, "Update from chat: " + update.getMessage().getChatId());
            BotLogger.info(TAG, "Update from user: " + update.getMessage().getFrom().getId());
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
        String content = update.getMessage().getText();
        Map<String, String> parameters = getParameters(content);
        BotDAO.Privacy privacy = BotDAO.Privacy.CHAT;
        Long id = update.getMessage().getChatId();
        if (parameters.containsKey(Commands.privacyParam)) {
            privacy = BotDAO.Privacy.USER;
            id = new Long(update.getMessage().getFrom().getId());
        }
        String name = content.split(" ")[content.split(" ").length - 1];

        boolean result = dao.remove(name, id, privacy);
        if (result) sendMessage("Succesfully deleted " + name, update.getMessage().getChatId());
        else sendMessage("Couldn't delete " + name, update.getMessage().getChatId());
    }

    private void onListCommand(Update update) {
        Map<String, String> parameters = getParameters(update.getMessage().getText());
        BotDAO.Privacy privacy = BotDAO.Privacy.CHAT;
        Long id = update.getMessage().getChatId();
        if (parameters.containsKey(Commands.privacyParam)) {
            privacy = BotDAO.Privacy.USER;
            id = new Long(update.getMessage().getFrom().getId());
        }

        StringBuilder sb = new StringBuilder();
        List<String> names = new ArrayList<>();
        if (dao.getAll(id, privacy) != null)
            names = dao.getAll(id, privacy).stream().map(Compiled::getName).collect(Collectors.toList());
        Collections.sort(names);
        sb.append("List: ").append(System.getProperty("line.separator"));
        for (String name : names) sb.append(name).append(System.getProperty("line.separator"));

        sendMessage(sb.toString(), update.getMessage().getChatId());
    }

    private void onJavacCommand(Update update) {
        String content = update.getMessage().getText();
        Map<String, String> parameters = getParameters(content);
        BotDAO.Privacy privacy = BotDAO.Privacy.CHAT;
        Long id = update.getMessage().getChatId();
        String name;
        if (parameters.containsKey(Commands.privacyParam)) {
            privacy = BotDAO.Privacy.USER;
            id = new Long(update.getMessage().getFrom().getId());
            content = content.replaceFirst(Commands.privacyParam, "");
        }

        //remove command
        content = content.replaceFirst(content.split(" ", 2)[0], "");
        if (parameters.containsKey(Commands.mainParam)) {
            name = parameters.get(Commands.mainParam);
            content = content.replaceFirst(Commands.mainParam, "");
            content = content.replaceFirst(name, "");

            StringBuilder sb = new StringBuilder();
            sb.append("public class ").append(name).append(" {");
            sb.append(" public static void main(String[] args) {");
            sb.append(content);
            sb.append("}}");
            content = sb.toString();
        }

        Code code = new Code(content);

        if (code.compile()) {
            if (dao.get(code.getName(), id, privacy) != null) {
                dao.remove(code.getName(), id, privacy);
            }
            dao.add(code.getCompiled(), id, privacy);
            sendMessage("Succesfully compiled!", update.getMessage().getChatId());
        } else {
            sendMessage("Compilation failed " + System.getProperty("line.separator") + code.getOut(),
                    update.getMessage().getChatId()
            );
        }
    }

    private void onJavaCommand(Update update) {
        String content = update.getMessage().getText();
        String[] pieces = content.split(" ");
        String name;
        Compiled compiled;
        int i = 1;

        Map<String, String> parameters = getParameters(content);
        BotDAO.Privacy privacy = BotDAO.Privacy.CHAT;
        Long id = update.getMessage().getChatId();
        if (parameters.containsKey(Commands.privacyParam)) {
            privacy = BotDAO.Privacy.USER;
            id = new Long(update.getMessage().getFrom().getId());
            i++;
        }

        name = pieces[i];

        String[] args;
        if (pieces.length > i + 1)
            args = Arrays.copyOfRange(pieces, i + 1, pieces.length);
        else
            args = new String[0];

        if (dao.isEmpty(id, privacy)) sendMessage("No scripts in database.", update.getMessage().getChatId());
        else if (!dao.contains(name, id, privacy)){
            sendMessage("Database doesn't contain script named '" + name + "'", update.getMessage().getChatId());
        } else {
            compiled = dao.get(name, id, privacy);
            compiled.run(args);
            sendMessage(compiled.getOut(), update.getMessage().getChatId());
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
        sb.append("Use '/javac -m Classname to write only to main method").append("\n");
        sb.append("Example:").append("\n");
        sb.append("'/javac -m HelloWorld System.out.println(\"Hello World!\");'").append("\n");
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
        Map<String, String> parameters = new HashMap<>();
        String[] pieces = text.split(" ");
        // [0] -> command Ex. /java
        // [i] -> parameter Ex. -p
        // [i + 1] -> parameter argument where applicable Ex. -c Test

        for (int i = 1; i < pieces.length; i++) {
            if (pieces[i].startsWith(Commands.mainParam) && pieces[i].length() == 2) {
                if (i + 1 < pieces.length
                        && !pieces[i + 1].startsWith(String.valueOf(Commands.paramInitChar))) {
                    parameters.put(pieces[i], pieces[i + 1]);
                    i++;
                }
            }
            else if (pieces[i].startsWith(Commands.privacyParam) && pieces[i].length() == 2) {
                parameters.put(pieces[i], null);
            } else break;
        }

        return parameters;
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
