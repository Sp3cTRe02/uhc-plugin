package com.sp3ctr3.uhc.uhcCuarenee.listeners;

import com.sp3ctr3.uhc.uhcCuarenee.tab.TabManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        TabManager.setDefaultTab(event.getPlayer());
    }
}
