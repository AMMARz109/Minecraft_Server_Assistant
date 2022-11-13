package me.ammardev.serverassistant.randomTeleport;

import me.ammardev.serverassistant.ServerAssistant;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class RtpCommandManger implements CommandExecutor {

    Map<Player, Integer> rtpCounter = new HashMap<>();
    public static Map<Player, Boolean> moveListener = new HashMap<>();
    Map<UUID, Integer> cooldown = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("rtp")){
            teleport(player);
        }
        return false;
    }

    void teleport(Player player) {
        if (cooldown.containsKey(player.getUniqueId())){
            if (cooldown.get(player.getUniqueId()) > 0){
                player.sendMessage(ChatColor.RED + "Please wait for more " + cooldown.get(player.getUniqueId()) + " seconds");
                return;
            }
        }
        if (!player.getWorld().getName().equalsIgnoreCase("world")) {
            player.sendMessage(ChatColor.RED + "Cant use that here");
            return;
        }

        final PotionEffect blind = new PotionEffect(PotionEffectType.BLINDNESS, 2000, 10);


        moveListener.put(player, false);

        player.playSound(player, Sound.BLOCK_ANVIL_PLACE, 1, 1);
        new BukkitRunnable() {
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
                    player.sendMessage(ChatColor.GREEN + "Rtp in " + i + " seconds, please don't move");
                    i--;
                }else {
                    Random random = new Random();

                    Location newLocation = null;

                    Block block = null;
                    do {
                        int x = random.nextInt(1000000);
                        int y = 255;
                        int z = random.nextInt(1000000);
                        newLocation = new Location(player.getWorld(), x, y, z);
                        int newY = player.getWorld().getHighestBlockYAt(newLocation);

                        newLocation.setY(newY);

                        block = player.getWorld().getBlockAt(newLocation);


                    } while (block.getType() == Material.LAVA || block.getType() == Material.WATER);

                    player.removePotionEffect(blind.getType());
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 10));

                    player.teleport(newLocation);
                    player.getWorld().createExplosion(player.getLocation(), 0, false, false);

                    startcooldown(player);
                    cancel();
                }
            }
        }.runTaskTimer(ServerAssistant.instance, 0L, 20L);


    }


    void startcooldown(Player player){

        new BukkitRunnable(){
            int i = 360;
            @Override
            public void run() {
                if (i >= 0){
                    cooldown.put(player.getUniqueId(), i);
                    i--;
                }else {
                    cancel();
                }
            }
        }.runTaskTimer(ServerAssistant.instance, 0L, 20L);
    }

}

/*

 */
