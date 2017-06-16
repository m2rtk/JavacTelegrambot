package bot;

public class Utils {
    public static String toMonospace(String text) {
        return  "```" + System.getProperty("line.separator") + text + System.getProperty("line.separator") + "```";
    }
}
