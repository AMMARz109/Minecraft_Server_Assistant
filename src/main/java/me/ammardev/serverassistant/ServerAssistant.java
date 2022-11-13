package me.ammardev.serverassistant;

import me.ammardev.serverassistant.home.HomeCommandManger;
import me.ammardev.serverassistant.home.HomeListener;
import me.ammardev.serverassistant.randomTeleport.RtpCommandManger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ServerAssistant extends JavaPlugin {

    public static ServerAssistant instance;
    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        try {
            setup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Bukkit.getServer().getConsoleSender().sendMessage("[Server Assistant] enabled");
        getCommand("home").setExecutor(new HomeCommandManger());
        getCommand("sethome").setExecutor(new HomeCommandManger());
        getCommand("rtp").setExecutor(new RtpCommandManger());
        getServer().getPluginManager().registerEvents(new HomeListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getServer().getConsoleSender().sendMessage("[Server Assistant] disabled");
    }


    void setup() throws IOException {
        File file;
        Path filePath = Paths.get("plugins/Server Assistant/home.json");
        Path dirPath = Paths.get("plugins/Server Assistant/");

        if (!dirPath.toFile().exists()){
            File dir = new File(dirPath.toString());
            dir.mkdirs();
        }

        if (!filePath.toFile().exists()){
            file = new File(filePath.toString());
            file.createNewFile();
        }else {
            file = filePath.toFile();
        }

    }


}
