package bot;

import bot.commands.*;
import bot.commands.parameters.JavaBackgroundParameter;
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

    public static final Map<String, Class> commands;

    public static final Map<String, Class> parameters;

    static {
        Map<String, Class> c = new HashMap<>();
        c.put(cmdInitChar + "help",   HelpCommand.class);
        c.put(cmdInitChar + "nice",   NiceCommand.class);
        c.put(cmdInitChar + "java",   JavaCommand.class);
        c.put(cmdInitChar + "javac",  JavacCommand.class);
        c.put(cmdInitChar + "list",   ListCommand.class);
        c.put(cmdInitChar + "delete", DeleteCommand.class);
        c.put(cmdInitChar + "up",     UpCommand.class);
        c.put(cmdInitChar + "kill",   KillCommand.class);
        commands = Collections.unmodifiableMap(c);

        Map<String, Class> p = new HashMap<>();
        p.put(paramInitChar + "p", PrivacyParameter.class);
        p.put(paramInitChar + "m", MainParameter.class);
        p.put(paramInitChar + "n", NoMonospaceFontParameter.class);
        p.put(paramInitChar + "b", JavaBackgroundParameter.class);
        parameters = Collections.unmodifiableMap(p);
    }
}
