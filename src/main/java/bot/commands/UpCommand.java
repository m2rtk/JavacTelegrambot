package bot.commands;

import bot.Commands;
import bot.commands.interfaces.StartTime;

import java.time.Instant;

public class UpCommand extends Command implements StartTime {

    private Long startTime;

    @Override
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    @Override
    public void execute() {
        if (startTime == null) throw new IllegalExecutionException();
        long t = Instant.now().getEpochSecond() - startTime;
        long sec = t % 60;
        long min = t % 3600 / 60;
        long hour = t % 86400 / 3600;
        long day = t / 86400;

        String output = "I've been up for " + t + " seconds." + System.getProperty("line.separator");
        output += "That's " + day + " days, " + hour + " hours, " + min + " minutes and " + sec + " seconds.";
        setOutput(output);
    }

    @Override
    public String getName() {
        return Commands.up;
    }

    @Override
    public String toString() {
        return "UpCommand{" +
                "startTime=" + startTime +
                "} ";
    }
}
