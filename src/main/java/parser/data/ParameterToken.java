package parser.data;

public class ParameterToken extends Token {
    private String parameter;
    private String argument;

    ParameterToken(String parameter, String argument) {
        this.parameter = parameter;
        this.argument = argument;
    }

    ParameterToken(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public String getValue() {
        return parameter;
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
