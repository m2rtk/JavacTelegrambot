package com.github.m2rtk.telegram.bot;

import com.github.m2rtk.telegram.bot.commands.*;
import com.github.m2rtk.telegram.bot.commands.parameters.MainParameter;
import com.github.m2rtk.telegram.bot.commands.parameters.PrivacyParameter;

import java.util.Collections;
import java.util.HashMap;
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
        Map<String, Class> commands = new HashMap<>();
        commands.put(help, helpCommandClass);
        commands.put(nice, niceCommandClass);
        commands.put(java, javaCommandClass);
        commands.put(javac, javacCommandClass);
        commands.put(list, listCommandClass);
        commands.put(delete, deleteCommandClass);
        commands.put(up, upCommandClass);
        allCommands = Collections.unmodifiableMap(commands);

        Map<String, Class> parameters = new HashMap<>();
        parameters.put(privacyParameter, privacyParameterClass);
        parameters.put(mainParameter, mainParameterClass);
        allParameters = Collections.unmodifiableMap(parameters);
    }
}
