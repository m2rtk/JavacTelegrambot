package bot;

import java.util.Arrays;
import java.util.List;

public class Commands {

    private Commands() {}

    public static final char initChar = '/';

    public static final char paramInitChar = '-';

    public static final String help = initChar + "help";

    public static final String nice = initChar + "nice";

    public static final String java = initChar + "java";

    public static final String javac = initChar + "javac";

    public static final String list = initChar + "list";

    public static final String delete = initChar + "delete";

    public static final String up = initChar + "up";

    public static final String privacyParam = paramInitChar + "p";

    public static final String mainParam = paramInitChar + "m";

    public static final List<String> allCommands   = Arrays.asList(help, nice, java, javac, list, delete, up);

    public static final List<String> allParameters = Arrays.asList(privacyParam, mainParam);
}
