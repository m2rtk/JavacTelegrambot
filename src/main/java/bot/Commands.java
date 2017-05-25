package bot;

import bot.commands.*;
import bot.commands.parameters.MainParameter;
import bot.commands.parameters.PrivacyParameter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Commands {

    private Commands() {}

    public static final char initChar = '/';

    public static final char paramInitChar = '-';

    public static final String help = initChar + "help";
    private static final Class helpCommandClass = HelpCommand.class;

    public static final String nice = initChar + "nice";
    private static final Class niceCommandClass = NiceCommand.class;

    public static final String java = initChar + "java";
    private static final Class javaCommandClass = JavaCommand.class;

    public static final String javac = initChar + "javac";
    private static final Class javacCommandClass = JavacCommand.class;

    public static final String list = initChar + "list";
    private static final Class listCommandClass = ListCommand.class;

    public static final String delete = initChar + "delete";
    private static final Class deleteCommandClass = DeleteCommand.class;

    public static final String up = initChar + "up";
    private static final Class upCommandClass = UpCommand.class;

    public static final String privacyParameter = paramInitChar + "p";
    private static final Class privacyParameterClass = PrivacyParameter.class;

    public static final String mainParameter = paramInitChar + "m";
    private static final Class mainParameterClass = MainParameter.class;

    public static final Map<String, Class> allCommands;

    public static final Map<String, Class> allParameters;

    static {
        allCommands = new HashMap<>();
        allCommands.put(help, helpCommandClass);
        allCommands.put(nice, niceCommandClass);
        allCommands.put(java, javaCommandClass);
        allCommands.put(javac, javacCommandClass);
        allCommands.put(list, listCommandClass);
        allCommands.put(delete, deleteCommandClass);
        allCommands.put(up, upCommandClass);

        allParameters = new HashMap<>();
        allParameters.put(privacyParameter, privacyParameterClass);
        allParameters.put(mainParameter, mainParameterClass);
    }
}
