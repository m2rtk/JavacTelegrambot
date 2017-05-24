package bot.commands;

import bot.Commands;
import bot.commands.interfaces.Command;
import bot.commands.interfaces.Parameter;

import java.time.Instant;

public class UpCommand extends Command {

    private final long startTime;
    private String output;

    public UpCommand(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public void execute() {
        long t = Instant.now().getEpochSecond() - startTime;
        long sec = t % 60;
        long min = t % 3600 / 60;
        long hour = t % 86400 / 3600;
        long day = t / 86400;

        output = "I've been up for " + t + " seconds." + System.getProperty("line.separator");
        output += "That's " + day + " days, " + hour + " hours, " + min + " minutes and " + sec + " seconds.";
    }

    @Override
    public String getOutput() {
        return output;
    }

    @Override
    public String getName() {
        return Commands.up;
    }

    @Override
    public String toString() {
        return "UpCommand{" +
                "startTime=" + startTime +
                ", output='" + output + '\'' +
                '}';
    }
}
