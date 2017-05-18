package parser;

public class Token {
    private String value;
    private String argument;

    public Token(String value, String argument) {
        this.value = value;
        this.argument = argument;
    }

    public Token(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public String getArgument() {
        return argument;
    }

    @Override
    public String toString() {
        return "Token{" +
                "value='" + value + '\'' +
                ", argument=" + argument +
                '}';
    }
}
