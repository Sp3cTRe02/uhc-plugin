package com.sp3ctr3.uhc.uhcCuarenee;

import com.sp3ctr3.uhc.uhcCuarenee.commands.Commands;
import com.sp3ctr3.uhc.uhcCuarenee.listeners.PlayerListener;
import com.sp3ctr3.uhc.uhcCuarenee.logic.SkinChanger;
import com.sp3ctr3.uhc.uhcCuarenee.tab.TabManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        
        SkinChanger.initialize();
        
        Commands UhcCommands = new Commands();
        getCommand("uhc").setExecutor(UhcCommands);
        getCommand("uhc").setTabCompleter(UhcCommands);

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        
        TabManager.setDefaultTabForAll();
    }

    @Override
    public void onDisable() {
    }

    public static Main getInstance() {
        return instance;
    }
}
