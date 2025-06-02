package ua.starman.easylogin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import ua.starman.easylogin.auther.AuthWorker;
import ua.starman.easylogin.auther.PlayerData;
import ua.starman.easylogin.utils.Utils;
import ua.starman.easylogin.utils.Vars;
import ua.starman.easylogin.utils.translator.Translation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class RegisterCommand implements CommandExecutor {
    Translation translation = new Translation("commands.register");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            List<MetadataValue> metadataList = ((Player) sender).getMetadata("auth_register");
            if (metadataList.isEmpty()) {
                sender.sendMessage(Utils.parseMessage(translation.getString("already_registered")));
                return true;
            }
            for (MetadataValue need_register : metadataList) {
                if (need_register.asBoolean()) {
                    if (args.length > 1) {
                        if (args[0].equals(args[1])) {
                            String password;

                            if (Objects.equals(Vars.encode, "SHA256")) {
                                password = Utils.encodeToSHA256(args[0]);
                            } else {
                                password = args[0];
                            }

                            PlayerData playerData = new PlayerData(((Player) sender).getUniqueId(),
                                    sender.getName(), password,
                                    AuthWorker.getIPAddress(Objects.requireNonNull(((Player) sender).getPlayer())),
                                    LocalDateTime.now());
                            playerData.save();

                            ((Player) sender).setMetadata("auth_block",
                                    new FixedMetadataValue(Vars.plugin, false));
                            ((Player) sender).setMetadata("auth_register",
                                    new FixedMetadataValue(Vars.plugin, false));

                            sender.sendMessage(Utils.parseMessage(translation.getString("register_success")));
                        } else {
                            sender.sendMessage(Utils.parseMessage(translation.getString("not_same_passwords")));
                        }
                    } else {
                        sender.sendMessage(Utils.parseMessage(translation.getString("password_not_confirmed")));
                    }
                } else {
                    sender.sendMessage(Utils.parseMessage(translation.getString("already_registered")));
                }
            }
        }
        return false;
    }
}
