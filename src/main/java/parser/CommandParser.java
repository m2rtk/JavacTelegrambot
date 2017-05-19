package parser;

import java.util.*;

import static bot.Commands.*;

/**
 *   /COMMAND( PARAMETER (ARGUMENT)?)? ARGUMENT?
 * /\/[a-zA-Z]+( -[a-zA-Z]( [a-zA-Z]+)?) [a-zA-Z]+?/g
 */
public class CommandParser {
    private final static String TERMINATOR = "\0";
    private String input;
    private int pos;
    private State state;
    private boolean needsNext, needsArgument;

    private Token.CommandToken command;
    private Map<String, Token.ParameterToken> parameters;

    private Token lastParam;

    private enum State {
        START, FREE, PARG
    }

    public CommandParser(String input) {

//        this.input = new ArrayList<>();
//        this.input.addAll(Arrays.asList(input.split("\\s+")));
//        this.input.add(TERMINATOR);
        this.input = input + TERMINATOR;

        this.pos = 0;
        this.state = State.START;

        this.needsNext = true;
        this.needsArgument = false;

        this.parameters = new HashMap<>();
    }

    private String nextToken() {
        String token;

        String[] pieces = input.split("\\s+", 2);

//        if (pieces.length < 2)
//            throw new ParserException("Ran out of input while parsing.");

        token = pieces[0];
        if (pieces.length > 1) input = pieces[1];
        else input = "";

        return token.trim();
    }

//    private String readRemaining(String token) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(token).append(" ");
//        while (pos < input.size()) sb.append(input.get(pos++)).append(" ");
//        return sb.toString().trim();
//    }

    public void parse() {
        String token;
        while (needsNext) {
            token = nextToken();
            System.out.println("---------------------------");
            System.out.println("token = " + token);
            System.out.println("input = " + input);
//            if (pos >= input.size())
//                throw new ParserException("Ran out of input while parsing.");
//
//            token = input.get(pos++);


            switch (state) {
                case START:
                    handleStart(token);
                    break;
                case FREE:
                    handleFree(token);
                    break;
                case PARG:
                    handlePArg(token);
                    break;
            }
        }
        if (needsArgument) throw new ParserException("Expected argument for command.");
    }

    private void handleStart(String token) {
        if (token.equals(TERMINATOR))
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
        if (token.equals(TERMINATOR)) {
            needsNext = false;
            return;
        }

        if (token.charAt(0) == paramInitChar) {
//            System.out.println(token);
            switch (token) {
                case mainParam:
                    state = State.PARG;
                case privacyParam:
                    break;
                default:
                    throw new ParserException("Unknown parameter " + token);
            }
            Token.ParameterToken param = Token.parameter(token);
            lastParam = param;
            parameters.put(token, param);
        } else { // if not parameter, then argument for command
            if (needsArgument) {
                String remaining = token;//readRemaining(token);
                if (!input.isEmpty()) remaining = token + " " + input.replace(TERMINATOR, "");
                command.setArgument(remaining);
                needsArgument = false;
            }
            needsNext = false;
        }
    }

    private void handlePArg(String token) {
        if (token.equals(TERMINATOR))
            throw new ParserException("Expected parameter argument. Reached end of input.");

        if (token.charAt(0) == paramInitChar) //todo ponder on the necessity of this
            throw new ParserException("Expected parameter argument. Got parameter " + token);

        lastParam.setArgument(token);
        state = State.FREE;
    }

    public Token.CommandToken getCommand() {
        return command;
    }

    public Map<String, Token.ParameterToken> getParameters() {
        return parameters;
    }
}
