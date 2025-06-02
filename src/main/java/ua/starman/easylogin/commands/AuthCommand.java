package ua.starman.easylogin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.starman.easylogin.EasyAuth;
import ua.starman.easylogin.auther.PlayerData;
import ua.starman.easylogin.utils.Utils;
import ua.starman.easylogin.utils.Vars;
import ua.starman.easylogin.utils.translator.Translator;

public class AuthCommand implements CommandExecutor {
    private static final Translator translator = EasyAuth.translator;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        PlayerData playerData;

        if (args.length > 0) {
            if (args[0].equals("reload")) {
                System.out.println(Vars.pluginTab + "Reloading plugin...");
                if (sender instanceof Player) {
                    sender.sendMessage(Utils.parseMessage("Reloading plugin..."));
                }

                EasyAuth.getPlugin(EasyAuth.class).reloadConfig();
                EasyAuth.translator.loadConfig();
                return true;
            }
        }

        if (args.length == 0 && (sender instanceof Player)) {
            playerData = PlayerData.get(((Player) sender).getUniqueId());
        } else if (args.length > 0 && sender.isOp()) {
            Player player = sender.getServer().getPlayer(args[0]);
            if (player != null) {
                playerData = PlayerData.get(player.getUniqueId());
            } else {
                sender.sendMessage(Utils.parseMessage(translator.getString("commands.auth.not_registered")));
                return false;
            }
        } else {
            // TODO
            return false;
        }

        assert playerData != null;

        sender.sendMessage(
                ChatColor.BLUE + "------------",
                ChatColor.AQUA + "UUID: " + ChatColor.RESET + playerData.uuid,
                ChatColor.AQUA + translator.getString("commands.auth.player_info.nickname") + ChatColor.RESET + playerData.name,
                ChatColor.AQUA + translator.getString("commands.auth.player_info.last_ip") + ChatColor.RESET + playerData.ip.toString(),
                ChatColor.AQUA + translator.getString("commands.auth.player_info.last_join") + ChatColor.RESET + playerData.lastLogin.toString(),
                ChatColor.BLUE + "------------"
        );
        return false;
    }
}
