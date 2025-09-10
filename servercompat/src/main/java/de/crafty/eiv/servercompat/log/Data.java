package de.crafty.eiv.servercompat.log;

import org.bukkit.Bukkit;

public class Data {

    public static final String PLUGIN_PREFIX = "\u00a77[\u00a76Extended ItemView\u00a77] ";



    public static void log(String info){
        Bukkit.getConsoleSender().sendMessage(PLUGIN_PREFIX + "\u00a7a" + info);
    }

    public static void warn(String warn){
        Bukkit.getConsoleSender().sendMessage(PLUGIN_PREFIX + "\u00a7c" + warn);
    }

    public static void error(String error){
        Bukkit.getConsoleSender().sendMessage(PLUGIN_PREFIX + "\u00a74" + error);
    }
}
