package bot;

import bot.commands.*;
import bot.commands.parameters.MainParameter;
import bot.commands.parameters.NoMonospaceFontParameter;
import bot.commands.parameters.PrivacyParameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Commands {

    private Commands() {}

    public static final char cmdInitChar = '/';

    public static final char paramInitChar = '-';

    public static final Map<String, Class> allCommands;

    public static final Map<String, Class> allParameters;

    static {
        Map<String, Class> commands = new HashMap<>();
        commands.put(cmdInitChar + "help",   HelpCommand.class);
        commands.put(cmdInitChar + "nice",   NiceCommand.class);
        commands.put(cmdInitChar + "java",   JavaCommand.class);
        commands.put(cmdInitChar + "javac",  JavacCommand.class);
        commands.put(cmdInitChar + "list",   ListCommand.class);
        commands.put(cmdInitChar + "delete", DeleteCommand.class);
        commands.put(cmdInitChar + "up",     UpCommand.class);
        allCommands = Collections.unmodifiableMap(commands);

        Map<String, Class> parameters = new HashMap<>();
        parameters.put(paramInitChar + "p", PrivacyParameter.class);
        parameters.put(paramInitChar + "m", MainParameter.class);
        parameters.put(paramInitChar + "n", NoMonospaceFontParameter.class);
        allParameters = Collections.unmodifiableMap(parameters);
    }
}
