package javac;

public class JavaFile {
    private String className;
    private String source;

    public JavaFile(String source) {
        this.source = source;
        this.className = parseClassName();
    }

    private String parseClassName() { // FIXME: 07.06.2017 pls, think of 'class Name' and comments before class declaration
        int i = source.indexOf("public class ");
        if (i == -1) return "";
        return source.substring(i + 13).split("\\{")[0].trim();
    }

    public String getSource() {
        return source;
    }

    public String getClassName() {
        return className;
    }
}
