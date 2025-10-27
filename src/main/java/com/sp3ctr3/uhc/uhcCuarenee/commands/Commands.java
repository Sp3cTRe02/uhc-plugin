package com.sp3ctr3.uhc.uhcCuarenee.commands;

import com.sp3ctr3.uhc.uhcCuarenee.logic.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Commands implements CommandExecutor, TabCompleter {
    // Enum para los subcomandos
    private enum Subcommand {
        START("start"),
        STOP("stop"),
        SCATTER("scatter");

        private final String name;
        Subcommand(String name) { this.name = name; }
        public String getName() { return name; }
        public static Subcommand fromString(String input) {
            for (Subcommand sub : Subcommand.values()) {
                if (sub.name.equalsIgnoreCase(input)) {
                    return sub;
                }
            }
            return null;
        }
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    )
    {
        if(args.length == 0) {
            sender.sendMessage("§6[UHC] §fUso: /uhc <start|stop|scatter>");
            return true;
        }
        Subcommand sub = Subcommand.fromString(args[0]);

        if (sub == null) {
            sender.sendMessage("§cSubcomando desconocido. Usa: /uhc <start|stop|scatter>");
            return true;
        }

        switch (sub) {
            case START:
                sender.sendMessage("§aIniciando partida de UHC...");
                GameManager.startGame();
                break;
            case STOP:
                GameManager.stopGame();
                break;
        }

        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> subcommands = new ArrayList<>();
        for (Subcommand sub : Subcommand.values()) {
            subcommands.add(sub.getName());
        }

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String sub : subcommands) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
            return completions;
        }

        return Collections.emptyList();
    }
}
