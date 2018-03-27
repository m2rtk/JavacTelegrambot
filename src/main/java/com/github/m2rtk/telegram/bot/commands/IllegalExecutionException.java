package com.github.m2rtk.telegram.bot.commands;

/**
 * Thrown when Command.execute() is called without proper setup.
 */
public class IllegalExecutionException extends RuntimeException {
    public IllegalExecutionException() {
        super();
    }
}
