package com.sp3ctr3.uhc.uhcCuarenee;

import com.sp3ctr3.uhc.uhcCuarenee.commands.Commands;
import com.sp3ctr3.uhc.uhcCuarenee.listeners.PlayerListener;
import com.sp3ctr3.uhc.uhcCuarenee.tab.TabManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        Commands UhcCommands = new Commands();
        getCommand("uhc").setExecutor(UhcCommands);
        getCommand("uhc").setTabCompleter(UhcCommands);
        
        // Registrar listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        
        TabManager.setDefaultTabForAll();
        getLogger().info("✅ UHCPlugin habilitado correctamente.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("❌ UHCPlugin deshabilitado.");
    }

    public static Main getInstance() {
        return instance;
    }
}
