package parser;

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


    public static class CommandToken extends Token {
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


    public static class ParameterToken extends Token {
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
}


