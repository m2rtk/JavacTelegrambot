package parser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static bot.Commands.*;


/**
 *   /COMMAND( PARAMETER (ARGUMENT)?)? ARGUMENT?
 * /\/[a-zA-Z]+( -[a-zA-Z]( [a-zA-Z]+)?) [a-zA-Z]+?/g
 */
public class CommandParser {
    private List<String> input;
    private int pos;
    private State state;
    private boolean needsNext, needsArgument;

    private Token command;
    private Set<Token> parameters;

    private Token lastParam;

    private enum State {
        START, FREE, PARG
    }

    public CommandParser(String input) {
        this.input = Arrays.asList(input.split(" "));
        this.pos = 0;
        this.state = State.START;

        this.needsNext = this.pos < this.input.size();
        this.needsArgument = false;

        this.parameters = new HashSet<>();
    }

    public void parse() {
        while (needsNext) {
            if (pos >= input.size()) throw new ParserException("Ran out of input while parsing.");
            String token = input.get(pos++);

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
        if (token.charAt(0) != initChar)
            throw new ParserException("Command must start with " + initChar);

        switch (token) {
            case up:
            case help:
            case nice:
                needsNext = false;
            case delete:
            case javac:
            case java:
                needsArgument = true;
            case list:
                state = State.FREE;
                command = new Token(token);
                break;
            default:
                throw new ParserException("Unknown command " + token);
        }
    }

    private void handleFree(String token) {
        if (token.charAt(0) == paramInitChar) {
            switch (token) {
                case mainParam:
                    state = State.PARG;
                case privacyParam:
                    Token param = new Token(token);
                    lastParam = param;
                    parameters.add(param);
                    break;
                default:
                    throw new ParserException("Unknown parameter " + token);
            }
        } else { // if not parameter, then argument for command
            command.setArgument(readRemaining(token));
            needsArgument = false;
            needsNext = false;
        }
    }

    private void handlePArg(String token) {
        if (token.charAt(0) == paramInitChar) //todo ponder on the necessity of this
            throw new ParserException("Expected parameter argument. Got parameter " + token);

        lastParam.setArgument(token);
        state = State.FREE;
    }

    private String readRemaining(String token) {
        StringBuilder sb = new StringBuilder();
        sb.append(token).append(" ");
        while (pos < input.size()) sb.append(input.get(pos)).append(" ");
        return sb.toString().trim();
    }

    public Token getCommand() {
        return command;
    }

    public Set<Token> getParameters() {
        return parameters;
    }
}
