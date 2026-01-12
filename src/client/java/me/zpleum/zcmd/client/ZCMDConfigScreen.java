package me.zpleum.zcmd.client;

import me.zpleum.zcmd.config.CommandEntry;
import me.zpleum.zcmd.config.ZCMDConfig.HudAlignment;
import me.zpleum.zcmd.config.ZCMDConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZCMDConfigScreen extends Screen {
    private final Screen parent;
    private final ZCMDConfig config;
    private final List<CommandEntry> tempCommands;

    private enum Tab { COMMANDS, APPEARANCE, ABOUT }
    private Tab currentTab = Tab.COMMANDS;
    private CommandEntry editingEntry = null;

    private TextFieldWidget addCmdField, addIntervalField;
    private TextFieldWidget editCmdField, editIntervalField;
    private final Map<CommandEntry, ButtonWidget> deleteButtons = new HashMap<>();

    public ZCMDConfigScreen(Screen parent, ZCMDConfig config) {
        super(Text.literal("§6§l⚡ ZCMD Dashboard"));
        this.parent = parent;
        this.config = config;
        this.tempCommands = new ArrayList<>(config.commands);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257) {
            if (addCmdField != null && addCmdField.isFocused()) {
                addNewCommand();
                return true;
            }
            if (editingEntry != null && editCmdField.isFocused()) {
                saveEdit();
                return true;
            }
        }

        if (keyCode == 256 && editingEntry != null) {
            editingEntry = null;
            init();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void addNewCommand() {
        try {
            String cmd = addCmdField.getText().trim();
            if (!cmd.isEmpty()) {
                tempCommands.add(new CommandEntry(cmd, Long.parseLong(addIntervalField.getText())));
                addCmdField.setText("");
                init();
            }
        } catch (Exception ignored) {}
    }

    private void saveEdit() {
        editingEntry.command = editCmdField.getText();
        editingEntry.intervalSeconds = Long.parseLong(editIntervalField.getText());
        editingEntry = null;
        init();
    }

    @Override
    protected void init() {
        clearChildren();
        deleteButtons.clear();
        int centerX = width / 2;

        int navY = 30;
        addNavButton(centerX - 120, navY, "📂 Commands", Tab.COMMANDS);
        addNavButton(centerX - 40, navY, "🎨 Appearance", Tab.APPEARANCE);
        addNavButton(centerX + 40, navY, "ℹ About", Tab.ABOUT);

        if (currentTab == Tab.COMMANDS) initCommandTab(centerX);
        else if (currentTab == Tab.APPEARANCE) initAppearanceTab(centerX);
        else initAboutTab(centerX);

        addDrawableChild(ButtonWidget.builder(Text.literal(config.enabled ? "§azCMD Enabled" : "§6zCMD Disabled"),
                        btn -> { config.enabled = !config.enabled; init(); })
                .dimensions(centerX - 100, height - 55, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("§aSave"), btn -> {
            config.commands.clear(); config.commands.addAll(tempCommands);
            config.save(); client.setScreen(parent);
        }).dimensions(centerX - 105, height - 30, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("§cCancel"), btn -> client.setScreen(parent))
                .dimensions(centerX + 5, height - 30, 100, 20).build());
    }

    private void addNavButton(int x, int y, String label, Tab tab) {
        boolean active = currentTab == tab;
        addDrawableChild(ButtonWidget.builder(Text.literal((active ? "§6" : "§7") + label),
                        btn -> { currentTab = tab; init(); })
                .dimensions(x, y, 80, 20).build());
    }

    private void initCommandTab(int centerX) {
        int y = 60;
        for (CommandEntry entry : tempCommands) {
            addDrawableChild(ButtonWidget.builder(Text.literal("§f/" + entry.command + " §e" + entry.intervalSeconds + "s"),
                    btn -> { editingEntry = entry; init(); }).dimensions(centerX - 100, y, 175, 20).build());
            ButtonWidget delBtn = ButtonWidget.builder(Text.literal("🗑"), btn -> {
                if (Screen.hasShiftDown()) { tempCommands.remove(entry); init(); }
            }).dimensions(centerX + 80, y, 20, 20).build();
            deleteButtons.put(entry, delBtn);
            addDrawableChild(delBtn);
            y += 22;
        }

        y += 10;
        addCmdField = new TextFieldWidget(textRenderer, centerX - 100, y, 100, 20, Text.empty());
        addCmdField.setPlaceholder(Text.literal("§8Command..."));
        addSelectableChild(addCmdField);
        addIntervalField = new TextFieldWidget(textRenderer, centerX + 5, y, 40, 20, Text.empty());
        addIntervalField.setText("60");
        addSelectableChild(addIntervalField);
        addDrawableChild(ButtonWidget.builder(Text.literal("§a+ Add"), btn -> {
            try {
                String cmd = addCmdField.getText().trim();
                if (!cmd.isEmpty()) { tempCommands.add(new CommandEntry(cmd, Long.parseLong(addIntervalField.getText()))); init(); }
            } catch (Exception ignored) {}
        }).dimensions(centerX + 50, y, 50, 20).build());

        if (editingEntry != null) setupEditOverlay(centerX);
    }

    private void initAppearanceTab(int centerX) {
        int y = 65;
        addDrawableChild(ButtonWidget.builder(Text.literal("HUD Display: " + (config.showHud ? "§aON" : "§cOFF")),
                btn -> { config.showHud = !config.showHud; init(); }).dimensions(centerX - 100, y, 200, 20).build());

        y += 25;
        addDrawableChild(ButtonWidget.builder(Text.literal("Position: §e" + config.alignment.name()),
                btn -> {
                    int next = (config.alignment.ordinal() + 1) % HudAlignment.values().length;
                    config.alignment = HudAlignment.values()[next];
                    init();
                }).dimensions(centerX - 100, y, 200, 20).build());

        y += 25;
        addValueControl(centerX - 100, y, "Opacity", config.hudOpacity, 10, 100, (val) -> config.hudOpacity = val);
        y += 25;
        addValueControl(centerX - 100, y, "Scale", (int)(config.hudScale * 100), 10, 200, (val) -> config.hudScale = val / 100f);
    }

    private void addValueControl(int x, int y, String label, int current, int min, int max, java.util.function.Consumer<Integer> setter) {
        addDrawableChild(ButtonWidget.builder(Text.literal("§7" + label + ": §e" + current + (label.equals("Scale") ? "%" : "")), btn -> {})
                .dimensions(x, y, 110, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("-"), btn -> { setter.accept(Math.max(min, current - 10)); init(); })
                .dimensions(x + 115, y, 40, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("+"), btn -> { setter.accept(Math.min(max, current + 10)); init(); })
                .dimensions(x + 160, y, 40, 20).build());
    }

    private void initAboutTab(int centerX) {
    }

    private void setupEditOverlay(int centerX) {
        int editY = height / 2;
        editCmdField = new TextFieldWidget(textRenderer, centerX - 100, editY, 140, 20, Text.empty());
        editCmdField.setText(editingEntry.command);
        addSelectableChild(editCmdField);
        editIntervalField = new TextFieldWidget(textRenderer, centerX + 45, editY, 30, 20, Text.empty());
        editIntervalField.setText(String.valueOf(editingEntry.intervalSeconds));
        addSelectableChild(editIntervalField);
        addDrawableChild(ButtonWidget.builder(Text.literal("§6Update"), btn -> {
            editingEntry.command = editCmdField.getText(); editingEntry.intervalSeconds = Long.parseLong(editIntervalField.getText());
            editingEntry = null; init();
        }).dimensions(centerX + 80, editY, 40, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean shift = Screen.hasShiftDown();
        deleteButtons.values().forEach(btn -> { btn.setMessage(Text.literal(shift ? "§c§l🗑" : "§8🗑")); btn.active = shift; });

        context.fillGradient(0, 0, width, height, 0xEE101010, 0xEE050505);
        context.fill(0, 0, width, 55, 0x66000000);

        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 10, 0xFFFFFF);

        if (currentTab == Tab.APPEARANCE) {
            context.fill(width / 2 + 110, 65, width - 10, 160, 0x44FFFFFF);
            context.drawCenteredTextWithShadow(textRenderer, "§7[ Live HUD Preview ]", width - (width - (width / 2 + 110)) / 2 - 10, 75, 0xFFFFFF);
        }

        if (editingEntry != null) {
            context.fill(0, 0, width, height, 0xCC000000);
            context.drawCenteredTextWithShadow(textRenderer, "§6§l✎ EDITING MODE", width / 2, height / 2 - 30, 0xFFFFFF);
            editCmdField.render(context, mouseX, mouseY, delta);
            editIntervalField.render(context, mouseX, mouseY, delta);
            super.render(context, mouseX, mouseY, delta);
        } else if (currentTab == Tab.COMMANDS) {
            addCmdField.render(context, mouseX, mouseY, delta);
            addIntervalField.render(context, mouseX, mouseY, delta);
            context.drawTextWithShadow(textRenderer, "§a§l+ Quick Add:", width / 2 - 100, addCmdField.getY() - 12, 0xFFFFFF);
        }
    }
}