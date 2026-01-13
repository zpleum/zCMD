package me.zpleum.zcmd.config;

public class CommandEntry {

    public String command;
    public long intervalSeconds;

    public transient long nextRunTick = 0;

    public CommandEntry(String command, long intervalSeconds) {
        this.command = command;
        this.intervalSeconds = Math.max(1, intervalSeconds);
    }

    public long intervalTicks() {
        long sec = Math.max(1, intervalSeconds);
        return sec * 20L;
    }
}
