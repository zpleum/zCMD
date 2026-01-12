package me.zpleum.zcmd.config;

public class CommandEntry {

    public String command;
    public long intervalSeconds;

    public transient long nextRunTick = 0;

    public CommandEntry(String command, long intervalSeconds) {
        this.command = command;
        this.intervalSeconds = intervalSeconds;
    }

    public long intervalTicks() {
        return intervalSeconds * 20L;
    }
}
