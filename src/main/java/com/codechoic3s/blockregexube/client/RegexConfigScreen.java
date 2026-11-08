package com.codechoic3s.blockregexube.client;

import com.codechoic3s.blockregexube.Config;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RegexConfigScreen extends Screen {
    private EditBox regexField;
    private EditBox radiusField;
    private EditBox maxBlocksField;
    private EditBox redField;
    private EditBox greenField;
    private EditBox blueField;
    private EditBox alphaField;
    private Button toggleButton;
    private Button throughWallsButton;

    protected RegexConfigScreen() {
        super(Component.literal("BlockRegexube Config"));
    }


    private static String booleanToStr(boolean value) {
        return value ? "ON" : "OFF";
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 40;
        int spacing = 35;

        regexField = new EditBox(this.font, centerX - 125, startY, 250, 20,
                Component.literal("Regex Pattern"));
        regexField.setValue(Config.REGEX_PATTERN.get());
        regexField.setMaxLength(256);
        this.addWidget(regexField);

        toggleButton = Button.builder(
                Component.literal("ESP: " + booleanToStr(Config.ENABLED.get())),
                btn -> {
                    boolean newVal = !Config.ENABLED.get();
                    Config.ENABLED.set(newVal);
                    btn.setMessage(Component.literal("ESP: " + booleanToStr(newVal)));
                }
        ).bounds(centerX - 125, startY + spacing, 80, 20).build();
        this.addRenderableWidget(toggleButton);

        radiusField = new EditBox(this.font, centerX - 30, startY + spacing, 70, 20,
                Component.literal("Scan Radius"));
        radiusField.setValue(String.valueOf(Config.SCAN_RADIUS.get()));
        this.addWidget(radiusField);

        maxBlocksField = new EditBox(this.font, centerX + 55, startY + spacing, 70, 20,
                Component.literal("Max Blocks"));
        maxBlocksField.setValue(String.valueOf(Config.MAX_BLOCKS.get()));
        this.addWidget(maxBlocksField);

        redField = new EditBox(this.font, centerX - 125, startY + spacing * 2, 50, 20,
                Component.literal("Red"));
        redField.setValue(String.valueOf(Config.RED.get()));
        this.addWidget(redField);

        greenField = new EditBox(this.font, centerX - 60, startY + spacing * 2, 50, 20,
                Component.literal("Green"));
        greenField.setValue(String.valueOf(Config.GREEN.get()));
        this.addWidget(greenField);

        blueField = new EditBox(this.font, centerX+5, startY + spacing * 2, 50, 20,
                Component.literal("Blue"));
        blueField.setValue(String.valueOf(Config.BLUE.get()));
        this.addWidget(blueField);

        alphaField = new EditBox(this.font, centerX + 75, startY + spacing * 2, 50, 20,
                Component.literal("Alpha"));
        alphaField.setValue(String.valueOf(Config.ALPHA.get()));
        this.addWidget(alphaField);

        throughWallsButton = Button.builder(
                Component.literal("Walls: " + booleanToStr(Config.RENDER_THROUGH_WALLS.get())),
                btn -> {
                    boolean newVal = !Config.RENDER_THROUGH_WALLS.get();
                    Config.RENDER_THROUGH_WALLS.set(newVal);
                    btn.setMessage(Component.literal("Walls: " + (newVal ? "ON" : "OFF")));
                }
        ).bounds(centerX +20, startY + spacing * 3, 70, 20).build();
        this.addRenderableWidget(throughWallsButton);

        toggleButton = Button.builder(
                Component.literal("Forging Tooltip: " + (Config.FORGING_TOOLTIP.get() ? "SHOWN" : "HIDDEN")),
                btn -> {
                    boolean newVal = !Config.FORGING_TOOLTIP.get();
                    Config.FORGING_TOOLTIP.set(newVal);
                    btn.setMessage(Component.literal("Forging Tooltip: " + (newVal ? "SHOWN" : "HIDDEN")));
                }
        ).bounds(centerX - 125, startY + spacing * 3, 130, 20).build();
        this.addRenderableWidget(toggleButton);

        this.addRenderableWidget(Button.builder(
                Component.literal("Save & Close"),
                btn -> saveAndClose()
        ).bounds(centerX - 50, this.height - 30, 100, 20).build());
    }

    private void saveAndClose() {
        try {
            Config.REGEX_PATTERN.set(regexField.getValue());
            Config.SCAN_RADIUS.set(Integer.parseInt(radiusField.getValue()));
            Config.MAX_BLOCKS.set(Integer.parseInt(maxBlocksField.getValue()));
            Config.RED.set(Double.parseDouble(redField.getValue()));
            Config.GREEN.set(Double.parseDouble(greenField.getValue()));
            Config.BLUE.set(Double.parseDouble(blueField.getValue()));
            Config.ALPHA.set(Double.parseDouble(alphaField.getValue()));
        } catch (NumberFormatException ignored) {}
        this.minecraft.setScreen(null);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, centerX, 15, 0xFFFFFF);
        guiGraphics.drawString(this.font, "Regex Pattern:", centerX - 125, 30, 0xAAAAAA);
        guiGraphics.drawString(this.font, "Scan Radius:", centerX - 30, 65, 0xAAAAAA);
        guiGraphics.drawString(this.font, "Max Blocks:", centerX + 55, 65, 0xAAAAAA);
        guiGraphics.drawString(this.font, "R", centerX - 125, 100, 0xAAAAAA);
        guiGraphics.drawString(this.font, "G", centerX - 60, 100, 0xAAAAAA);
        guiGraphics.drawString(this.font, "B", centerX + 5, 100, 0xAAAAAA);
        guiGraphics.drawString(this.font, "A", centerX + 75, 100, 0xAAAAAA);

        regexField.render(guiGraphics, mouseX, mouseY, partialTick);
        radiusField.render(guiGraphics, mouseX, mouseY, partialTick);
        maxBlocksField.render(guiGraphics, mouseX, mouseY, partialTick);
        redField.render(guiGraphics, mouseX, mouseY, partialTick);
        greenField.render(guiGraphics, mouseX, mouseY, partialTick);
        blueField.render(guiGraphics, mouseX, mouseY, partialTick);
        alphaField.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
