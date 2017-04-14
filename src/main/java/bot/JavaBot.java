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

import static dao.BotDAO.Privacy;
import static dao.BotDAO.Privacy.CHAT;
import static dao.BotDAO.Privacy.USER;

public class JavaBot extends TelegramLongPollingBot {
    private static final String TAG = "JAVABOT";

    // Non-final for testing purposes.
    private static BotDAO dao = new WriteToDiskBotDAO();

    @Override
    public void onUpdateReceived(Update update) {
        BotLogger.info(TAG, "Update received!");

        if (!update.hasMessage()) {
            BotLogger.info(TAG, "No message, Update end");
            return;
        }

        if (update.getMessage().isCommand()) {
            BotLogger.info(TAG, "Update from chat: " + update.getMessage().getChatId()
                                         + " user: " + update.getMessage().getFrom().getId()
                                         + System.getProperty("line.separator")
                                         + "content: " + update.getMessage().getText());

            executeCommand(update);
        }
    }

    private void executeCommand(Update update) {
        String command = update.getMessage().getText().split(" ", 2)[0];

        if (command.startsWith(Commands.help))        onHelpCommand(update);
        else if (command.startsWith(Commands.nice))   onNiceCommand(update);
        else if (command.startsWith(Commands.javac))  onJavacCommand(update);
        else if (command.startsWith(Commands.java))   onJavaCommand(update);
        else if (command.startsWith(Commands.list))   onListCommand(update);
        else if (command.startsWith(Commands.delete)) onDeleteCommand(update);
    }

    private void onDeleteCommand(Update update) {
        String content = update.getMessage().getText();
        Map<String, String> parameters = getParameters(update.getMessage().getText());
        Privacy privacy = parameters.containsKey(Commands.privacyParam) ? USER : CHAT;

        String name = content.split(" ")[content.split(" ").length - 1];

        runDeleteCommand(update, privacy, name);
    }

    private void runDeleteCommand(Update update, Privacy privacy, String className) {
        BotLogger.info(TAG, "Executing Delete command in chat: " + update.getMessage().getChatId()
                + " with privacy: " + privacy + " classname: " + className);
        Long id = privacy == CHAT ? update.getMessage().getChatId() : new Long(update.getMessage().getFrom().getId());
        boolean result = dao.remove(className, id, privacy);

        if (result) sendMessage("Successfully deleted " + className, update.getMessage().getChatId());
        else sendMessage("Couldn't delete " + className, update.getMessage().getChatId());
    }

    private void onListCommand(Update update) {
        Map<String, String> parameters = getParameters(update.getMessage().getText());
        Privacy privacy = parameters.containsKey(Commands.privacyParam) ? USER : CHAT;

        runListCommand(update, privacy);
    }

    private void runListCommand(Update update, Privacy privacy) {
        BotLogger.info(TAG, "Executing List command in chat: " + update.getMessage().getChatId()
                + " with privacy: " + privacy);
        Long id = privacy == CHAT ? update.getMessage().getChatId() : new Long(update.getMessage().getFrom().getId());

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
        Privacy privacy = CHAT;
        if (parameters.containsKey(Commands.privacyParam)) {
            privacy = USER;
            content = content.replaceFirst(Commands.privacyParam, "");
        }

        //remove command
        content = content.replaceFirst(content.split(" ", 2)[0], "");
        if (parameters.containsKey(Commands.mainParam)) {
            String name = parameters.get(Commands.mainParam);
            content = content.replaceFirst(Commands.mainParam, "");
            content = content.replaceFirst(name, "");

            StringBuilder sb = new StringBuilder();
            sb.append("public class ").append(name).append(" {");
            sb.append(" public static void main(String[] args) {");
            sb.append(content);
            sb.append("}}");
            content = sb.toString();
        }

        if (content.trim().isEmpty()) {
            sendMessage("No input found", update.getMessage().getChatId());
            return;
        }

        runJavacCommand(update, privacy, content);
    }

    private void runJavacCommand(Update update, Privacy privacy, String content) {
        BotLogger.info(TAG, "Executing Javac command in chat: " + update.getMessage().getChatId()
                + " with privacy: " + privacy + " content: " + content);
        Long id = privacy == CHAT ? update.getMessage().getChatId() : new Long(update.getMessage().getFrom().getId());
        Code code = new Code(content, privacy, id);

        if (code.compile()) {
            if (dao.get(code.getName(), id, privacy) != null) {
                dao.remove(code.getName(), id, privacy);
            }
            dao.add(code.getCompiled(), id, privacy);
            sendMessage("Successfully compiled!", update.getMessage().getChatId());
        } else {
            sendMessage("Compilation failed " + System.getProperty("line.separator") + code.getOut(),
                    update.getMessage().getChatId()
            );
        }
    }

    private void onJavaCommand(Update update) {
        String content = update.getMessage().getText();
        Map<String, String> parameters = getParameters(content);
        String[] pieces = content.split(" ");
        int i = 1;

        Privacy privacy = CHAT;
        if (parameters.containsKey(Commands.privacyParam)) {
            privacy = USER;
            i++;
        }

        String name = pieces[i];

        if (name.trim().isEmpty()) {
            sendMessage("No class name found", update.getMessage().getChatId());
            return;
        }

        String[] args;
        if (pieces.length > i + 1)
            args = Arrays.copyOfRange(pieces, i + 1, pieces.length);
        else
            args = new String[0];

        runJavaCommand(update, privacy, name, args);
    }

    private void runJavaCommand(Update update, Privacy privacy, String className, String[] args) {
        BotLogger.info(TAG, "Executing Java command in chat: " + update.getMessage().getChatId()
                + " with privacy: " + privacy + " className: " + className + " args: " + Arrays.asList(args));

        Long id = privacy == CHAT ? update.getMessage().getChatId() : new Long(update.getMessage().getFrom().getId());
        Compiled compiled = dao.get(className, id, privacy);

        if (compiled == null) {
            sendMessage("Database doesn't contain script named '" + className + "'", update.getMessage().getChatId());
            return;
        }

        compiled.run(args);
        sendMessage(compiled.getOut(), update.getMessage().getChatId());
    }

    private void onNiceCommand(Update update) {
        BotLogger.info(TAG, "Nice command from chat: " + update.getMessage().getChatId());
        sendMessage("nice", update.getMessage().getChatId());
    }

    private void onHelpCommand(Update update) {
        BotLogger.info(TAG, "Help command from chat: " + update.getMessage().getChatId());
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
        BotLogger.info(TAG, "Sending message '" + message + "' to chat: " + chatId);
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
        return Config.JAVABOT_USER;
    }

    @Override
    public String getBotToken() {
        return Config.JAVABOT_TOKEN;
    }
}
