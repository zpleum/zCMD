package me.zpleum.zcmd.client;

import me.zpleum.zcmd.config.CommandEntry;
import me.zpleum.zcmd.config.ZCMDConfig.HudAlignment;
import me.zpleum.zcmd.util.ModMetadataUtil;
import me.zpleum.zcmd.util.TimeUtil;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ZCMDHud {

    private static final int BASE_COLOR = 0xFFFFFFFF;
    private static final int PADDING = 5;

    public static void register() {
        HudRenderCallback.EVENT.register(ZCMDHud::render);
    }

    private static int applyOpacity(int color, int opacityPercent) {
        int alpha = (int) (255 * (Math.max(0, Math.min(100, opacityPercent)) / 100f));
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.textRenderer == null || mc.world == null || mc.player == null) return;
        if (ZcmdClient.CONFIG == null || !ZcmdClient.CONFIG.showHud) return;

        var cfg = ZcmdClient.CONFIG;
        int color = applyOpacity(BASE_COLOR, cfg.hudOpacity);

        long currentTick = mc.world.getTime();

        List<Text> lines = new ArrayList<>();
        lines.add(Text.literal("§8( §6§l⚡ §r§6zCMD §8) ").append(Text.literal(cfg.enabled ? "§aEnabled" : "§cDisabled")));

        if (!cfg.enabled) {
            lines.add(Text.literal("§8• §7Paused"));
        } else if (cfg.commands.isEmpty()) {
            lines.add(Text.literal("§8• §7No commands set"));
        } else {
            for (CommandEntry entry : cfg.commands) {
                long remainingTick = Math.max(0, entry.nextRunTick - currentTick);
                long remainingMs = remainingTick * 50L;

                lines.add(Text.literal("§8• §f/" + entry.command)
                        .append(Text.literal(" §8→ §e" + TimeUtil.format(remainingMs))));
            }
        }

        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();
        float scale = cfg.hudScale;
        int lineHeight = (int) ((mc.textRenderer.fontHeight + 2) * scale);

        int maxTextWidth = 0;
        for (Text line : lines) {
            maxTextWidth = Math.max(maxTextWidth, (int) (mc.textRenderer.getWidth(line) * scale));
        }

        int startX, startY;

        if (cfg.alignment == HudAlignment.TOP_RIGHT || cfg.alignment == HudAlignment.BOTTOM_RIGHT) {
            startX = screenWidth - maxTextWidth - PADDING;
        } else {
            startX = PADDING;
        }

        if (cfg.alignment == HudAlignment.BOTTOM_LEFT) {
            startY = screenHeight - (lines.size() * lineHeight) - PADDING - 15;
        }
        else if (cfg.alignment == HudAlignment.BOTTOM_RIGHT) {
            startY = screenHeight - (lines.size() * lineHeight) - PADDING;
        }
        else {
            startY = PADDING;
        }

        var matrices = context.getMatrices();

        matrices.pushMatrix();

        matrices.scale(scale, scale);

        float scaledX = startX / scale;
        float scaledY = startY / scale;

        for (Text text : lines) {
            context.drawText(mc.textRenderer, text, (int) scaledX, (int) scaledY, color, true);
            scaledY += mc.textRenderer.fontHeight + 2;
        }

        matrices.popMatrix();

        int authorsY = screenHeight - mc.textRenderer.fontHeight - 5;
        String authors = String.join(", ", ModMetadataUtil.authors());
        context.drawText(mc.textRenderer, Text.literal("§8" + ModMetadataUtil.modName() + " v" + ModMetadataUtil.version() + " §8by " + authors), 5, authorsY, applyOpacity(0xFFFFFFFF, 40), true);
    }
}