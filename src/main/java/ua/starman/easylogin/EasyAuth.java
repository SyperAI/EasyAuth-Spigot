package ua.starman.easylogin;

import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ua.starman.easylogin.commands.AuthCommand;
import ua.starman.easylogin.commands.LoginCommand;
import ua.starman.easylogin.commands.RegisterCommand;
import ua.starman.easylogin.events.AuthEvents;
import ua.starman.easylogin.filters.CommandsFilter;
import ua.starman.easylogin.filters.Log4JFilter;
import ua.starman.easylogin.utils.UpdateChecker;
import ua.starman.easylogin.utils.Utils;
import ua.starman.easylogin.utils.Vars;
import ua.starman.easylogin.utils.translator.Translator;

import java.util.Objects;
import java.util.logging.Logger;

public final class EasyAuth extends JavaPlugin {
    public static Translator translator = new Translator();

    public static void setupConsoleFilter(Logger logger) {
        // Try to set the log4j filter
        try {
            Class.forName("org.apache.logging.log4j.core.filter.AbstractFilter");
            setLog4JFilter();
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            // log4j is not available
            logger.info("You're using Minecraft 1.6.x or older, Log4J support will be disabled");
            CommandsFilter filter = new CommandsFilter();
            logger.setFilter(filter);
            Bukkit.getLogger().setFilter(filter);
            Logger.getLogger("Minecraft").setFilter(filter);
        }
    }

    // Set the console filter to remove the passwords
    private static void setLog4JFilter() {
        org.apache.logging.log4j.core.Logger logger;

        logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        logger.addFilter(new Log4JFilter());
    }

    @Override
    public void onEnable() {
        setupConsoleFilter(getLogger());

        // Plugin startup logic
        saveDefaultConfig();
        translator.loadConfig();

        Utils.checkDirs(Vars.dataDir);

        new UpdateChecker(this, 113725).checkUpdate();

        registerCommands();
        registerEvents();
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("register")).setExecutor(new RegisterCommand());
        Objects.requireNonNull(getCommand("login")).setExecutor(new LoginCommand());
        Objects.requireNonNull(getCommand("auth")).setExecutor(new AuthCommand());

    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new AuthEvents(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
