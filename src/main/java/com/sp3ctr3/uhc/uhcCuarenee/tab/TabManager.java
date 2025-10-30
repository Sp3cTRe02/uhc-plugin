package com.sp3ctr3.uhc.uhcCuarenee.tab;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TabManager {

    private static BukkitTask gamingTabTask = null;
    private static final Random random = new Random();
    private static final Map<Player, String> playerIcons = new HashMap<>();
    private static final int FIXED_NAME_LENGTH = 10;
    private static final String OBFUSCATED = "§k";
    private static final ChatColor[] ICON_COLORS = {
            ChatColor.RED, ChatColor.DARK_RED,
            ChatColor.GOLD, ChatColor.YELLOW,
            ChatColor.GREEN, ChatColor.DARK_GREEN,
            ChatColor.AQUA, ChatColor.DARK_AQUA,
            ChatColor.BLUE, ChatColor.DARK_BLUE,
            ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE,
            ChatColor.WHITE, ChatColor.GRAY
    };


    public static void updateTabForAll(String header, String footer) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateTab(player, header, footer);
        }
    }

    public static void updateTab(@NotNull Player player, String header, String footer) {
        player.setPlayerListHeader(header);
        player.setPlayerListFooter(footer);
    }

    public static void setTabName(@NotNull Player player, String name) {
        player.setPlayerListName(name);
    }

    public static void setDefaultTab(@NotNull Player player) {
        player.setPlayerListHeader(
                ChatColor.DARK_PURPLE + "" + ChatColor.DARK_PURPLE + "UHC Cuarenee"
        );
        player.setPlayerListFooter(
                ChatColor.GOLD + "¡Bienvenidos a todos!"
        );
        player.setPlayerListName(ChatColor.GREEN + player.getName());
    }

    public static void setDefaultTabForAll() {
        stopGamingTabUpdater();
        
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            Scoreboard scoreboard = manager.getMainScoreboard();
            Objective healthObjective = scoreboard.getObjective("vida");
            if (healthObjective != null) {
                healthObjective.unregister();
            }
        }
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            setDefaultTab(player);
        }
        playerIcons.clear();
    }

    private static String getObfuscatedName(Player player) {
        StringBuilder obfuscated = new StringBuilder();
        ChatColor randomColor = ICON_COLORS[random.nextInt(ICON_COLORS.length)];

        obfuscated.append(randomColor).append(OBFUSCATED);

        for (int i = 0; i < FIXED_NAME_LENGTH; i++) {
            obfuscated.append((char) ('A' + random.nextInt(26)));
        }

        return obfuscated.toString();
    }

    private static void setupHealthScoreboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard scoreboard = manager.getMainScoreboard();
        
        Objective healthObjective = scoreboard.getObjective("vida");
        if (healthObjective != null) {
            healthObjective.unregister();
        }

        healthObjective = scoreboard.registerNewObjective("vida", "health", ChatColor.RED + "❤");
        
        healthObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        
        healthObjective.setRenderType(RenderType.HEARTS);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
    }

    public static void setGamingTab(@NotNull Player player) {

        player.setPlayerListHeader(
                ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "UHC Cuarenee"
        );
        player.setPlayerListFooter(
                ChatColor.GOLD + "¡Que gane el mejor!"
        );

        String obfuscatedName = getObfuscatedName(player);
        player.setPlayerListName(obfuscatedName);
        
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            Scoreboard scoreboard = manager.getMainScoreboard();
            player.setScoreboard(scoreboard);
        }
    }

    public static void setGamingTabForAll() {
        setupHealthScoreboard();
        for (Player player : Bukkit.getOnlinePlayers()) {
            setGamingTab(player);
        }
    }

    public static void startGamingTabUpdater() {
    }

    public static void stopGamingTabUpdater() {
        if (gamingTabTask != null) {
            gamingTabTask.cancel();
            gamingTabTask = null;
        }
    }
}