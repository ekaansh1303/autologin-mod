package com.autologin.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;

public class AutoLoginConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("autologin");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("autologin.json");

    private boolean enabled = true;
    private String password = "";
    private String command = "/login {password}";
    private int delayTicks = 40; // 2 seconds default

    public static AutoLoginConfig load() {
        if (CONFIG_PATH.toFile().exists()) {
            try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
                AutoLoginConfig config = GSON.fromJson(reader, AutoLoginConfig.class);
                if (config != null) return config;
            } catch (Exception e) {
                LOGGER.error("Failed to load AutoLogin config", e);
            }
        }
        AutoLoginConfig defaults = new AutoLoginConfig();
        defaults.save();
        return defaults;
    }

    public void save() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(this, writer);
        } catch (Exception e) {
            LOGGER.error("Failed to save AutoLogin config", e);
        }
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    public int getDelayTicks() { return delayTicks; }
    public void setDelayTicks(int delayTicks) { this.delayTicks = delayTicks; }
}
