package me.ammardev.serverassistant.home;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.ammardev.serverassistant.ServerAssistant;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class HomeCommandManger implements CommandExecutor {

    Map<Player, Integer> homeCounter = new HashMap<>();
    Map<UUID, Integer> cooldownCounter = new HashMap<>();
    static Map<Player, Boolean> moveListener = new HashMap<>();
    File file = Paths.get("plugins/Server Assistant/home.json").toFile();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;


        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("sethome")){
            try {
                saveHome(new Home(player.getLocation().getX(),
                        player.getLocation().getY(),
                        player.getLocation().getZ(),
                        player.getUniqueId()));
                player.sendMessage(ChatColor.GREEN + "Home set successfully");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        if (cmd.getName().equalsIgnoreCase("home")){
            try {
                teleport(player);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }



    void saveHome(Home home) throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        boolean found = false;

        List<Home> list = new ArrayList<>(getHomeList());
        for (Home h: list) {
            if (h.getPlayerUUID().equals(home.getPlayerUUID())){
                h.setX(home.getX());
                h.setY(home.getY());
                h.setZ(home.getZ());
                found = true;
            }
        }
        if (!found){
            list.add(home);
        }

        try(Writer writer = new FileWriter(file)){
            writer.write(gson.toJson(list));
            writer.close();
        }
    }

    List<Home> getHomeList() throws FileNotFoundException {
        if (file.length() <= 0){
            return new ArrayList<>();
        }
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        Home[] homes = gson.fromJson(new FileReader(file), Home[].class);
        return Arrays.asList(homes);
    }

    List<UUID> getUUIDList() throws FileNotFoundException {
        List<UUID> list = new ArrayList<>();
        for (Home home: getHomeList()) {
            list.add(home.getPlayerUUID());
        }
        return list;
    }

    void teleport(Player player) throws FileNotFoundException {
        if (!getUUIDList().contains(player.getUniqueId())){
            player.sendMessage(ChatColor.RED + "Please set home first");
            return;
        }

        if (cooldownCounter.containsKey(player.getUniqueId())){
            if (cooldownCounter.get(player.getUniqueId()) > 0){
                player.sendMessage(ChatColor.RED + "Please wait for more " + cooldownCounter.get(player.getUniqueId()) + " seconds");
                return;
            }
        }


        moveListener.put(player, false);

        Home homeLoader = null;
        for (Home h: getHomeList()) {
            if (h.getPlayerUUID().equals(player.getUniqueId())){
                homeLoader = h;
            }
        }

        Home home = homeLoader;
        player.playSound(player, Sound.BLOCK_ANVIL_PLACE, 1, 1);
        new BukkitRunnable(){

            final PotionEffect blind = new PotionEffect(PotionEffectType.BLINDNESS, 2000, 10);
            int i = 5;
            boolean used = false;
            @Override
            public void run() {
                if (moveListener.get(player)){
                    player.removePotionEffect(blind.getType());
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 10));
                    player.sendMessage(ChatColor.RED + "You have moved, tp canceled");
                    cancel();
                }
                if (moveListener.get(player)){
                    return;
                }
                if (i > 0){
                    if (!used){
                        player.addPotionEffect(blind);
                        used = true;
                    }
                    homeCounter.put(player, i);
                    player.sendMessage(ChatColor.GREEN + "Teleporting in " + i + " seconds");
                    i--;
                }else {
                    player.removePotionEffect(blind.getType());
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 10));
                    assert home != null;
                    player.teleport(new Location(Bukkit.getWorld("world"),
                            home.getX(),
                            home.getY(),
                            home.getZ()));
                    player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    player.sendMessage(ChatColor.GREEN + "Welcome home");
                    player.getWorld().createExplosion(player.getLocation(), 0, false, false);
                    startCooldown(player);
                    cancel();


                }
            }

        }.runTaskTimer(ServerAssistant.instance, 0L, 20L);
    }

    void startCooldown(Player player){

        new BukkitRunnable(){
            int i = 260;
            @Override
            public void run() {
                if (i >= 0){
                    cooldownCounter.put(player.getUniqueId(), i);
                    i--;
                }else {
                    cancel();
                }
            }
        }.runTaskTimer(ServerAssistant.instance, 0L, 20L);
    }
}
