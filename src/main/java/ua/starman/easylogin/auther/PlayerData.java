package ua.starman.easylogin.auther;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ua.starman.easylogin.utils.LocalDateTimeAdapter;
import ua.starman.easylogin.utils.Vars;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;

public class PlayerData {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    public UUID uuid;
    public String name;
    public String password;
    public InetAddress ip;
    public LocalDateTime lastLogin;

    public PlayerData(UUID uuid, String name, String password, InetAddress ip, LocalDateTime lastLogin) {
        this.uuid = uuid;
        this.name = name;
        this.password = password;
        this.ip = ip;
        this.lastLogin = lastLogin;
    }

    public static boolean check(UUID playerUUID) {
        File playerSaveFile = new File(Vars.dataDir, playerUUID + ".json");
        File playerSaveFileOld = new File(Vars.dataDirOld, playerUUID + ".json");

        return playerSaveFile.exists() || playerSaveFileOld.exists();
    }

    public static PlayerData get(UUID playerUUID) {
        File playerSaveFile = new File(Vars.dataDir, playerUUID.toString() + ".json");
        File playerSaveFileOld = new File(Vars.dataDirOld, playerUUID + ".json");
        File file;

        if (!playerSaveFile.exists()) {
            file = playerSaveFileOld;
        } else {
            file = playerSaveFile;
        }

        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, PlayerData.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        assert uuid != null;
        File playerSaveFile = new File(Vars.dataDir, uuid + ".json");
        try (FileWriter writer = new FileWriter(playerSaveFile)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
