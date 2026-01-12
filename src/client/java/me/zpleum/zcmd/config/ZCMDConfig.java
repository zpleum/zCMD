package me.zpleum.zcmd.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ZCMDConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("zcmd.json")
            .toFile();

    public long intervalSeconds = 200;
    public int hudOpacity = 100;
    public boolean showHud = true;

    public enum HudAlignment { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

    public float hudScale = 1.0f;
    public HudAlignment alignment = HudAlignment.TOP_LEFT;

    public boolean enabled = true;
    public List<CommandEntry> commands = new ArrayList<>();

    public ZCMDConfig() {
        if (commands.isEmpty()) {
            commands.add(new CommandEntry("command", 200));
        }
    }

    public static ZCMDConfig load() {
        try {
            if (FILE.exists()) {
                ZCMDConfig cfg =
                        GSON.fromJson(new FileReader(FILE), ZCMDConfig.class);

                if (cfg.commands == null || cfg.commands.isEmpty()) {
                    cfg.commands = new ArrayList<>();
                    cfg.commands.add(new CommandEntry("command", 200));
                }
                return cfg;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ZCMDConfig();
    }

    public void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(this, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long intervalMs() {
        return intervalSeconds * 1000L;
    }
}
