package me.zpleum.zcmd.client;

import me.zpleum.zcmd.config.CommandEntry;
import me.zpleum.zcmd.config.ZCMDConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.PriorityQueue;

public class ZcmdClient implements ClientModInitializer {

    public static ZCMDConfig CONFIG;

    private static KeyBinding toggleKey;
    private static KeyBinding openGuiKey;

    private static Long lastWorldTime = null;

    private static final PriorityQueue<CommandEntry> queue =
            new PriorityQueue<>(Comparator.comparingLong(e -> e.nextRunTick));

    @Override
    public void onInitializeClient() {

        ZCMDHud.register();
        CONFIG = ZCMDConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zcmd.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_PERIOD,
                "category.zcmd"
        ));

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zcmd.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.zcmd"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            while (openGuiKey.wasPressed()) {
                client.setScreen(new ZCMDConfigScreen(client.currentScreen, CONFIG));
            }

            while (toggleKey.wasPressed()) {
                CONFIG.enabled = !CONFIG.enabled;
                CONFIG.save();

                if (CONFIG.enabled) {
                    rebuildQueue(client.world.getTime());
                } else {
                    queue.clear();
                }

                client.player.sendMessage(
                        Text.literal("§8( §6§l⚡ zCMD §8) ")
                                .append(Text.literal(CONFIG.enabled ? "§aEnabled" : "§cDisabled")),
                        false
                );
            }

            if (!CONFIG.enabled) {
                queue.clear();
                lastWorldTime = null;
                return;
            }

            long currentTick = client.world.getTime();

            if (lastWorldTime != null && currentTick < lastWorldTime) {
                rebuildQueue(currentTick);
            }

            lastWorldTime = currentTick;

            if (queue.isEmpty() && !CONFIG.commands.isEmpty()) {
                rebuildQueue(currentTick);
            }

            while (!queue.isEmpty()) {
                CommandEntry entry = queue.peek();
                if (entry.nextRunTick > currentTick) break;

                queue.poll();

                String cmd = normalizeCommand(entry.command);
                if (!cmd.isEmpty()) {
                    client.player.networkHandler.sendChatCommand(cmd);
                }

                // schedule ใหม่จาก tick ปัจจุบัน
                entry.nextRunTick = currentTick + entry.intervalTicks();
                queue.add(entry);
            }
        });
    }

    public static void rebuildQueue(long nowTick) {
        queue.clear();

        for (CommandEntry entry : CONFIG.commands) {
            if (entry == null || entry.command == null || entry.command.isBlank()) continue;

            long interval = entry.intervalTicks();
            entry.nextRunTick = nowTick + interval;
            queue.add(entry);
        }
    }

    private static String normalizeCommand(String raw) {
        return raw == null ? "" : raw.trim().replaceFirst("^/+", "");
    }
}
