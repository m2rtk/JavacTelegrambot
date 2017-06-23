package parser;

import bot.Commands;
import bot.Config;
import bot.commands.Command;
import bot.commands.interfaces.NeedsArgument;
import bot.commands.visitors.Parameter;

import java.util.HashSet;
import java.util.Set;

import static bot.Commands.cmdInitChar;
import static bot.Commands.paramInitChar;

public class CommandParser {
    private String input;
    private State state;
    private boolean needsNext, parsed;

    //output
    private Command command;
    private Set<Parameter> parameters;

    private NeedsArgument lastParameter;

    private enum State {
        START, FREE, ARG
    }

    public CommandParser(String input) {
        this.input = input;

        this.parameters = new HashSet<>();
        this.needsNext = true;
        this.parsed = false;
        this.state = State.START;
    }

    /**
     * Removes the first element bordered by whitespace from input and returns it.
     * @return String token
     */
    private String nextToken() { //// TODO: 18.06.2017 count whitespace and save int as variable
        String[] pieces = input.split("\\s+", 2); //split input in two by first whitespace
        input = pieces.length > 1 ? pieces[1] : "";
        return pieces[0].trim();
    }

    /**
     * Starts the parsing process.
     */
    public void parse() throws ParserException {
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

        if (command instanceof NeedsArgument)
            if (!((NeedsArgument) command).hasArgument())
                throw new ParserException("Expected argument for command. Reached end of input.");

        parsed = true;
    }

    private void handleStart(String token) throws ParserException {
        if (token.isEmpty())
            throw new ParserException("Expected command. Reached end of input.");

        if (token.charAt(0) != cmdInitChar)
            throw new ParserException("Command must start with " + cmdInitChar);

        token = token.replace("@" + Config.JAVABOT_USER, ""); //todo write tests for this

        if (Commands.commands.containsKey(token)) {
            this.command = (Command) construct(Commands.commands.get(token));
            this.state = State.FREE;
//        } else if (Character.isUpperCase(token.charAt(1))) {
//            throw new SpecialJavaCommandException();
        } else {
            throw new SpecialJavaCommandException();
//            throw new ParserException("Undefined command '" + token + "'");
        }
    }

    private void handleFree(String token) {
        if (token.isEmpty()) {
            needsNext = false;
            return;
        }

        if (Commands.parameters.keySet().contains(token)) {
            Parameter parameter = (Parameter) construct(Commands.parameters.get(token));

            if (parameter instanceof NeedsArgument) {
                this.state = State.ARG;
                this.lastParameter = (NeedsArgument) parameter;
            }

            this.parameters.add(parameter);
        } else { // not a parameter -> must be command argument
           end(token);
        }
    }

    private void handleArg(String token) throws ParserException {
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
        if (command instanceof NeedsArgument) {
            if (input.trim().isEmpty()) ((NeedsArgument) command).setArgument(token);
            else ((NeedsArgument) command).setArgument(token + " " + input.trim());
        }
        needsNext = false;
    }

    /**
     * Output method.
     * @return parsed Command
     * @throws RuntimeException if called before parse() method.
     */
    public Command getCommand() {
        if (!parsed) throw new RuntimeException("Parse must be called before this method.");
        return command;
    }

    /**
     * Output method.
     * @return set of parsed Parameters
     * @throws RuntimeException if called before parse() method.
     */
    public Set<Parameter> getParameters() {
        if (!parsed) throw new RuntimeException("Parse must be called before this method.");
        return parameters;
    }

    /**
     * Constructs an object of Class c.
     * Assumes that Class c has a constructor that takes 0 arguments.
     * Is meant to be used only for subclasses of Command or Parameter.
     * @param c class of object to construct.
     * @return Instance of class c
     */
    private static Object construct(Class c) {
        try {
            return c.getConstructors()[0].newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cant cast parameter " + e);
        }
    }
}
