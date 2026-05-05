package com.autologin.gui;

import com.autologin.AutoLoginMod;
import com.autologin.config.AutoLoginConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AutoLoginConfigScreen extends Screen {

    private final Screen parent;
    private AutoLoginConfig config;

    private TextFieldWidget passwordField;
    private TextFieldWidget commandField;
    private TextFieldWidget delayField;

    private ButtonWidget enableButton;
    private boolean showPassword = false;

    public AutoLoginConfigScreen(Screen parent) {
        super(Text.literal("AutoLogin Configuration"));
        this.parent = parent;
        this.config = AutoLoginMod.getConfig();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 60;
        int fieldWidth = 200;
        int fieldHeight = 20;
        int spacing = 36;

        // --- Enable/Disable Toggle ---
        enableButton = ButtonWidget.builder(
                getEnabledText(),
                btn -> {
                    config.setEnabled(!config.isEnabled());
                    btn.setMessage(getEnabledText());
                }
        ).dimensions(centerX - 100, startY, fieldWidth, 20).build();
        this.addDrawableChild(enableButton);

        // --- Password Field ---
        passwordField = new TextFieldWidget(
                this.textRenderer,
                centerX - fieldWidth / 2, startY + spacing,
                fieldWidth, fieldHeight,
                Text.literal("Password")
        );
        passwordField.setMaxLength(64);
        passwordField.setText(config.getPassword());
        passwordField.setRenderTextProvider((text, firstCharacterIndex) -> {
            if (showPassword) return Text.literal(text).asOrderedText();
            return Text.literal("*".repeat(text.length())).asOrderedText();
        });
        this.addDrawableChild(passwordField);

        // --- Show/Hide Password Button ---
        ButtonWidget togglePasswordBtn = ButtonWidget.builder(
                Text.literal("👁"),
                btn -> {
                    showPassword = !showPassword;
                    passwordField.setFocused(true);
                }
        ).dimensions(centerX + fieldWidth / 2 + 4, startY + spacing, 20, 20)
                .tooltip(Tooltip.of(Text.literal("Show/hide password")))
                .build();
        this.addDrawableChild(togglePasswordBtn);

        // --- Command Field ---
        commandField = new TextFieldWidget(
                this.textRenderer,
                centerX - fieldWidth / 2, startY + spacing * 2,
                fieldWidth, fieldHeight,
                Text.literal("Command")
        );
        commandField.setMaxLength(128);
        commandField.setText(config.getCommand());
        commandField.setTooltip(Tooltip.of(Text.literal("Use {password} as placeholder")));
        this.addDrawableChild(commandField);

        // --- Delay Field ---
        delayField = new TextFieldWidget(
                this.textRenderer,
                centerX - fieldWidth / 2, startY + spacing * 3,
                fieldWidth, fieldHeight,
                Text.literal("Delay")
        );
        delayField.setMaxLength(5);
        delayField.setText(String.valueOf(config.getDelayTicks()));
        delayField.setTooltip(Tooltip.of(Text.literal("Delay in ticks before sending (20 ticks = 1 second)")));
        this.addDrawableChild(delayField);

        // --- Save Button ---
        ButtonWidget saveBtn = ButtonWidget.builder(
                Text.literal("Save"),
                btn -> save()
        ).dimensions(centerX - 102, this.height - 40, 100, 20).build();
        this.addDrawableChild(saveBtn);

        // --- Cancel Button ---
        ButtonWidget cancelBtn = ButtonWidget.builder(
                Text.literal("Cancel"),
                btn -> this.close()
        ).dimensions(centerX + 2, this.height - 40, 100, 20).build();
        this.addDrawableChild(cancelBtn);
    }

    private Text getEnabledText() {
        return config.isEnabled()
                ? Text.literal("§aAutoLogin: ENABLED")
                : Text.literal("§cAutoLogin: DISABLED");
    }

    private void save() {
        config.setPassword(passwordField.getText());
        config.setCommand(commandField.getText().isEmpty() ? "/login {password}" : commandField.getText());
        try {
            int delay = Integer.parseInt(delayField.getText());
            config.setDelayTicks(Math.max(1, Math.min(delay, 600)));
        } catch (NumberFormatException e) {
            config.setDelayTicks(40);
        }
        config.save();
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        // Title
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("§6§lAutoLogin §r§7Configuration"),
                this.width / 2, 20, 0xFFFFFF
        );

        int centerX = this.width / 2;
        int startY = 60;
        int spacing = 36;
        int labelColor = 0xAAAAAA;

        // Field labels
        context.drawTextWithShadow(this.textRenderer, Text.literal("§7Password:"), centerX - 100, startY + spacing - 12, labelColor);
        context.drawTextWithShadow(this.textRenderer, Text.literal("§7Command (use {password}):"), centerX - 100, startY + spacing * 2 - 12, labelColor);
        context.drawTextWithShadow(this.textRenderer, Text.literal("§7Delay (ticks, 20 = 1s):"), centerX - 100, startY + spacing * 3 - 12, labelColor);

        // Hint
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("§8Press F8 anytime to open this menu"),
                this.width / 2, this.height - 56, 0x666666
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
