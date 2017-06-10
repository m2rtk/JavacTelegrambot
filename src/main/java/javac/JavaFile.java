package javac;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.ArrayDeque;
import java.util.Deque;

public class JavaFile {
    private String className;
    private String source;

    public JavaFile(String source) {
        this.source = source;
        this.className = parseClassName();
    }

    private String parseClassName() {
        try {
            CompilationUnit cu = JavaParser.parse(source);
            Deque<Node> stack = new ArrayDeque<>();
            stack.push(cu);
            while (!stack.isEmpty()) {
                Node node = stack.pop();
                stack.addAll(node.getChildNodes());
                if (node instanceof ClassOrInterfaceDeclaration) {
                    return node.getChildNodes().get(0).toString();
                }
            }
        } catch (ParseProblemException ignored) {
            // We want the error message from javac, not this parser.
            // If this parser fails, so will javac.
        }
        return "FailedToParseClassName";
    }

    public String getSource() {
        return source;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JavaFile javaFile = (JavaFile) o;

        return className.equals(javaFile.className) && source.equals(javaFile.source);
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + source.hashCode();
        return result;
    }
}
