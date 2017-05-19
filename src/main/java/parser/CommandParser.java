package parser;

import parser.data.CommandToken;
import parser.data.ParameterToken;
import parser.data.Token;

import java.util.*;

import static bot.Commands.*;

/**
 *   /COMMAND( PARAMETER (ARGUMENT)?)? ARGUMENT?
 * /\/[a-zA-Z]+( -[a-zA-Z]( [a-zA-Z]+)?) [a-zA-Z]+?/g
 */

// TODO: 19.05.2017 maybe rewrite this to be command and parameter agnostic
public class CommandParser {
    private final static String TERMINATOR = "\0";
    private String input;
    private State state;
    private boolean needsNext, needsArgument;

    private CommandToken command;
    private Map<String, ParameterToken> parameters; // TODO: 19.05.2017 Maybe replace with Map<String, String> <parameter, argument>

    private Token lastParam;

    private enum State {
        START, FREE, ARG
    }

    public CommandParser(String input) {
        this.input = input + TERMINATOR;

        this.state = State.START;

        this.needsNext = true;
        this.needsArgument = false;

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
                    throw new RuntimeException("Not possible. I hope. Invalid state.");
            }
        }
        if (needsArgument) throw new ParserException("Expected argument for command.");
    }

    private void handleStart(String token) {
        if (token.isEmpty())
            throw new ParserException("Expected command. Reached end of input.");

        if (token.charAt(0) != initChar)
            throw new ParserException("Command must start with " + initChar);

        token = token.replace("@BotMcBotfaceBot", ""); //todo write tests for this

        switch (token) {
            case up:
            case help:
            case nice:
                needsNext = false;
                break;
            case delete:
            case javac:
            case java:
                needsArgument = true;
            case list:
                state = State.FREE;
                break;
            default:
                throw new ParserException("Unknown command " + token);
        }

        command = Token.command(token);
    }

    private void handleFree(String token) {
        if (token.isEmpty()) {
            needsNext = false;
            return;
        }

        if (token.charAt(0) == paramInitChar) {
            switch (token) {
                case mainParam:
                    state = State.ARG;
                case privacyParam:
                    break;
                default:
                    throw new ParserException("Unknown parameter " + token);
            }
            ParameterToken param = Token.parameter(token);
            lastParam = param;
            parameters.put(token, param);
        } else { // if not parameter, then argument for command
            if (needsArgument) {
                if (!input.isEmpty()) command.setArgument(token + " " + input.replace(TERMINATOR, ""));
                else command.setArgument(token);
                needsArgument = false;
            }
            needsNext = false;
        }
    }

    private void handleArg(String token) {
        if (token.isEmpty())
            throw new ParserException("Expected parameter argument. Reached end of input.");

        if (token.charAt(0) == paramInitChar)
            throw new ParserException("Expected parameter argument. Got parameter " + token);

        lastParam.setArgument(token);
        state = State.FREE;
    }

    public CommandToken getCommand() {
        return command;
    }

    public Map<String, ParameterToken> getParameters() {
        return parameters;
    }
}
