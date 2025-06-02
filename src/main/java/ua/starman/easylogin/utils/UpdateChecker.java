package ua.starman.easylogin.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Consumer;

// From: https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates
public class UpdateChecker {

    private final JavaPlugin plugin;
    private final int resourceId;

    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream is = new URI("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId + "/~").toURL().openStream(); Scanner scann = new Scanner(is)) {
                if (scann.hasNext()) {
                    consumer.accept(scann.next());
                }
            } catch (IOException | URISyntaxException e) {
                plugin.getLogger().info("Unable to check for updates: " + e.getMessage());
            }
        });
    }

    private boolean checkTextVersionUpToDate(String version) {
        String[] serverVersionStr = Arrays.stream(version.split("\\D+")).filter(s -> !s.isEmpty()).toArray(String[]::new);
        String[] currentVersionStr = Arrays.stream(plugin.getDescription().getVersion().split("\\D+")).filter(s -> !s.isEmpty()).toArray(String[]::new);

        int serverMajor = Integer.parseInt(serverVersionStr[0]);
        int serverMinor = Integer.parseInt(serverVersionStr[1]);
        int serverPatch = Integer.parseInt(serverVersionStr[2]);

        int currentMajor = Integer.parseInt(currentVersionStr[0]);
        int currentMinor = Integer.parseInt(currentVersionStr[1]);
        int currentPatch = Integer.parseInt(currentVersionStr[2]);

        if (serverMajor <= currentMajor) {
            return true;
        } else if (serverMinor <= currentMinor) {
            return true;
        } else return serverPatch <= currentPatch;
    }

    public void checkUpdate() {
        this.getVersion(version -> {
            if (this.plugin.getDescription().getVersion().equals(version) || this.checkTextVersionUpToDate(version)) {
                this.plugin.getLogger().info("Plugin is up to date!");
            } else {
                this.plugin.getLogger().info("There is a new update available " +
                        this.plugin.getDescription().getVersion() +
                        " -> " + version);
                this.plugin.getLogger().info("Check https://www.spigotmc.org/resources/easylogin.113725/");
            }
        });
    }
}