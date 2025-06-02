package ua.starman.easylogin.utils;

import org.bukkit.plugin.Plugin;
import ua.starman.easylogin.EasyAuth;

import java.io.File;
import java.util.Objects;

public class Vars {
    public static Plugin plugin = EasyAuth.getPlugin(EasyAuth.class);
    public static String pluginTab = plugin.getConfig().getString("plugin.tab");
    public static File dataDir = new File(Objects.requireNonNull(plugin.getConfig().getString("data.dir")));
    public static File dataDirOld = new File("plugins/EasyAuth/player-data");
    public static String encode = plugin.getConfig().getString("data.method");
}
