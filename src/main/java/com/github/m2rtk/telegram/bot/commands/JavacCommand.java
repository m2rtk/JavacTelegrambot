package com.github.m2rtk.telegram.bot.commands;

import com.github.m2rtk.telegram.bot.Commands;
import com.github.m2rtk.telegram.bot.commands.interfaces.NeedsArgument;
import com.github.m2rtk.telegram.bot.commands.interfaces.NeedsDAO;
import com.github.m2rtk.telegram.bot.commands.interfaces.NeedsPrivacy;
import com.github.m2rtk.telegram.dao.BotDAO;
import com.github.m2rtk.telegram.dao.Privacy;
import com.github.m2rtk.telegram.javac.Compiler;
import com.github.m2rtk.telegram.javac.JavaFile;

public class JavacCommand extends Command implements NeedsArgument, NeedsPrivacy, NeedsDAO {
    private BotDAO dao;
    private String content;
    private Privacy privacy;
    private Long id;

    public void wrapContentInMain(String classname) {
        if (content == null) throw new NullPointerException("Content/argument must be set before calling this method.");
        this.content = String.format("public class %s { \n\tpublic static void main(String[] args) {\n\t\t%s\n\t}\n}", classname, content);
    }

    @Override
    public void execute() {
        if (content == null || content.isEmpty() || id == null || privacy == null || dao == null) throw new IllegalExecutionException();
        JavaFile javaFile = new JavaFile(content);
        Compiler compiler = new Compiler(javaFile);
        compiler.setClassPath(privacy, id);

        if (compiler.compile()) {
            if (dao.get(javaFile.getClassName(), id, privacy) != null) {
                dao.remove(javaFile.getClassName(), id, privacy);
            }
            dao.add(compiler.getOutputClass(), id, privacy);
            setOutput("Successfully compiled!");
        } else {
            setOutput("Compilation failed " + System.getProperty("line.separator") + compiler.getOutputMessage());
        }
    }

    @Override
    public String getName() {
        return Commands.javac;
    }

    @Override
    public void setPrivacy(Privacy privacy, Long id) {
        this.privacy = privacy;
        this.id = id;
    }

    @Override
    public void setArgument(String argument) {
        this.content = argument;
    }

    @Override
    public boolean hasArgument() {
        return this.content != null;
    }

    @Override
    public void setDAO(BotDAO dao) {
        this.dao = dao;
    }

    @Override
    public String toString() {
        return "JavacCommand{" +
                "com.github.m2rtk.telegram.dao=" + dao +
                ", content='" + content + '\'' +
                ", privacy=" + privacy +
                ", id=" + id +
                "} ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        JavacCommand that = (JavacCommand) o;

        if (dao != null ? !dao.equals(that.dao) : that.dao != null) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (privacy != that.privacy) return false;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dao != null ? dao.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (privacy != null ? privacy.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
