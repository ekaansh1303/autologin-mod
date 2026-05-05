package com.autologin;

import com.autologin.config.AutoLoginConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.message.MessageType;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoLoginMod implements ClientModInitializer {

    public static final String MOD_ID = "autologin";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static AutoLoginConfig config;

    private static KeyBinding openConfigKey;
    private static boolean loginScheduled = false;
    private static int loginDelay = 0;

    @Override
    public void onInitializeClient() {
        config = AutoLoginConfig.load();

        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autologin.openconfig",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                "category.autologin"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKey.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new com.autologin.gui.AutoLoginConfigScreen(client.currentScreen));
                }
            }

            if (loginScheduled && client.player != null) {
                loginDelay--;
                if (loginDelay <= 0) {
                    loginScheduled = false;
                    performLogin(client);
                }
            }
        });

        LOGGER.info("AutoLogin mod loaded!");
    }

    public static void scheduleLogin() {
        if (config.isEnabled() && !config.getPassword().isEmpty()) {
            loginScheduled = true;
            loginDelay = config.getDelayTicks();
            LOGGER.info("AutoLogin: Login scheduled in {} ticks", loginDelay);
        }
    }

    private static void performLogin(MinecraftClient client) {
        if (client.player == null) return;
        String command = config.getCommand().replace("{password}", config.getPassword());
        // Remove leading slash if present since sendCommand adds context
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        client.player.networkHandler.sendCommand(command);
        LOGGER.info("AutoLogin: Sent login command");
    }

    public static AutoLoginConfig getConfig() {
        return config;
    }
}
