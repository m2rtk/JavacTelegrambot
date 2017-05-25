package parser;

import bot.Commands;
import bot.commands.Command;
import bot.commands.JavaCommand;
import bot.commands.interfaces.Argument;
import bot.commands.parameters.Parameter;

import java.util.HashMap;
import java.util.Map;

import static bot.Commands.initChar;
import static bot.Commands.paramInitChar;

public class CommandParser {
    private String input;
    private State state;
    private boolean needsNext, parseCalled;

    //output
    private Command command;
    private Map<String, Parameter> parameters;

    private Argument lastParameter;

    private enum State {
        START, FREE, ARG
    }

    public CommandParser(String input) {
        this.input = input;
        this.state = State.START;

        this.needsNext = true;
        this.parseCalled = false;

        this.parameters = new HashMap<>();
    }

    private String nextToken() {
        String token;

        String[] pieces = input.split("\\s+", 2);

        token = pieces[0];
        if (pieces.length > 1) input = pieces[1];
        else input = "";

        return token.trim();
    }

    public void parse() {
        parseCalled = true;
        String token;
        while (needsNext) {
            token = nextToken();
            switch (state) {
                case START:
                    handleStart(token);
                    break;
                case FREE:
                    handleFree(token);
                    break;
                case ARG:
                    handleArg(token);
                    break;
                default:
                    throw new RuntimeException("Invalid state. Not possible. I hope.");
            }
        }

        if (command instanceof Argument)
            if (!((Argument) command).hasArgument())
                throw new ParserException("Expected argument for command. Reached end of input.");
    }

    private void handleStart(String token) {
        if (token.isEmpty())
            throw new ParserException("Expected command. Reached end of input.");

        if (token.charAt(0) != initChar)
            throw new ParserException("Command must start with " + initChar);

        token = token.replace("@BotMcBotfaceBot", ""); //todo write tests for this

        if (Commands.allCommands.containsKey(token)) {
            this.command = (Command) construct(Commands.allCommands.get(token));
            this.state = State.FREE;
        } else {
            // unknown command, lets assume its a special case of /java
            this.command = new JavaCommand();
            end(token.substring(1));
        }
    }

    private void handleFree(String token) {
        if (token.isEmpty()) {
            needsNext = false;
            return;
        }

        if (Commands.allParameters.keySet().contains(token)) {
            Parameter parameter = (Parameter) construct(Commands.allParameters.get(token));

            if (parameter instanceof Argument) {
                this.state = State.ARG;
                this.lastParameter = (Argument) parameter;
            }

            this.parameters.put(token, parameter);
        } else { // not a parameter -> must be command argument
           end(token);
        }
    }

    private void handleArg(String token) {
        if (token.isEmpty())
            throw new ParserException("Expected parameter argument. Reached end of input.");

        if (token.charAt(0) == paramInitChar)
            throw new ParserException("Expected parameter argument. Got parameter " + token);

        this.lastParameter.setArgument(token);
        this.state = State.FREE;
    }

    /**
     * Ends the parsing.
     * If command needs argument, sets token + remaining input as argument.
     * @param token current token.
     */
    private void end(String token) {
        if (command instanceof Argument) {
            if (input.trim().isEmpty()) ((Argument) command).setArgument(token);
            else ((Argument) command).setArgument(token + " " + input.trim());
        }
        needsNext = false;
    }

    private static Object construct(Class c) {
        try {
            return c.getConstructors()[0].newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cant cast parameter " + e);
        }
    }

    public Command getCommand() {
        if (!parseCalled) throw new RuntimeException("Parse must be called before this method.");
        return command;
    }

    public Map<String, Parameter> getParameters() {
        if (!parseCalled) throw new RuntimeException("Parse must be called before this method.");
        return parameters;
    }
}
