package com.sp3ctr3.uhc.uhcCuarenee.logic;

import com.sp3ctr3.uhc.uhcCuarenee.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class GameManager {

    private static boolean gameRunning = false;
    private static boolean countdownActive = false;

    public static void startGame() {
        if (gameRunning) {
            return;
        }

        countdownActive = true;
        new BukkitRunnable() {
            int countdown = 10;

            @Override
            public void run() {



                if (countdown > 0) {
                    String color = getColorFor(countdown);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendTitle(color + countdown, "§7¡Prepárate!", 0, 20, 0);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 1f);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * (countdown + 1), 10, false, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * (countdown + 1), 10, false, false, false));
                    }

                    countdown--;
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendTitle("§a¡La partida ha comenzado!", "", 10, 40, 10);
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
                        player.removePotionEffect(PotionEffectType.SLOWNESS);
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        player.setHealth(player.getMaxHealth());
                        player.setFoodLevel(20);
                        player.setSaturation(20f);
                    }

                    countdownActive = false;
                    gameRunning = true;
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L); // cada 20 ticks = 1 segundo
    }

    private static String getColorFor(int number) {
        switch (number) {
            case 10: return "§f";
            case 9:  return "§7";
            case 8:  return "§c";
            case 7:  return "§4";
            case 6:  return "§6";
            case 5:  return "§e";
            case 4:  return "§a";
            case 3:  return "§2";
            case 2:  return "§b";
            case 1:  return "§3";
            default: return "§f";
        }
    }

    public static boolean isGameRunning() {
        return gameRunning;
    }

    public static boolean isCountdownActive() {
        return countdownActive;
    }

    public static void stopGame() {
        if (!gameRunning) {
            Bukkit.broadcastMessage("§cNo hay ninguna partida en curso.");
            return;
        }

        gameRunning = false;
        Bukkit.broadcastMessage("§6[UHC] §cLa partida ha sido detenida.");
    }
}
