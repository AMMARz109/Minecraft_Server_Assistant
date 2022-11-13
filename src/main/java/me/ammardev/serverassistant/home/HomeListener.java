package me.ammardev.serverassistant.home;

import me.ammardev.serverassistant.randomTeleport.RtpCommandManger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class HomeListener implements Listener {

    @EventHandler
    void onMove(PlayerMoveEvent event){
        HomeCommandManger.moveListener.put(event.getPlayer(), true);
        RtpCommandManger.moveListener.put(event.getPlayer(), true);
    }
}
