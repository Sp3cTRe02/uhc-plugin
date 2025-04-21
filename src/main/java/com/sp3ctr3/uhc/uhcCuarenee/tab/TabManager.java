package com.sp3ctr3.uhc.uhcCuarenee.tab;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TabManager {

    public static void updateTabForAll(String header, String footer) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateTab(player, header, footer);
        }
    }

    public static void updateTab(Player player, String header, String footer) {
        player.setPlayerListHeader(header);
        player.setPlayerListFooter(footer);
    }

    public static void setTabName(Player player, String name) {
        player.setPlayerListName(name);
    }

    public static void setDefaultTab(Player player) {
        player.setPlayerListHeader(
                ChatColor.GOLD + "" + ChatColor.BOLD + "UHC Cuarenee"
        );
        player.setPlayerListFooter(
                ChatColor.GOLD + "¡Bienvenidos a todos!"
        );
        player.setPlayerListName(ChatColor.GREEN + player.getName());
    }

    public static void setDefaultTabForAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setDefaultTab(player);
        }
    }
}
