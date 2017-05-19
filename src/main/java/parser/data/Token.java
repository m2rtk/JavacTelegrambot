package parser.data;

public abstract class Token {

    public static CommandToken command(String value, String argument) {
        return new CommandToken(value, argument);
    }

    public static CommandToken command(String value) {
        return new CommandToken(value);
    }

    public static ParameterToken parameter(String value, String argument) {
        return new ParameterToken(value, argument);
    }

    public static ParameterToken parameter(String value) {
        return new ParameterToken(value);
    }

    public abstract String getValue();

    public abstract void setArgument(String argument);

    public abstract String getArgument();

}


