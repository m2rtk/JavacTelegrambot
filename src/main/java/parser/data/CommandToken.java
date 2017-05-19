package parser.data;

public class CommandToken extends Token {
    private String command;
    private String argument;

    CommandToken(String command, String argument) {
        this.command = command;
        this.argument = argument;
    }

    CommandToken(String command) {
        this.command = command;
    }

    @Override
    public String getValue() {
        return command;
    }

    @Override
    public void setArgument(String argument) {
        this.argument = argument;
    }

    @Override
    public String getArgument() {
        return argument;
    }
}
