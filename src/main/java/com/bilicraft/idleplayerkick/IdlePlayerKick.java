package com.bilicraft.idleplayerkick;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class IdlePlayerKick extends JavaPlugin implements Listener {
    private Essentials essentials;

    @Override
    public void onEnable() {
        // Plugin startup logic
        essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event){
       if(event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.KICK_FULL){
           User player= getLongestIdlePlayer();
           if(player == null){
               return;
           }
           player.getBase().kickPlayer("由于服务器当前人数已满且您处于暂离状态，因此您已被从服务器移出。");
           event.setLoginResult(AsyncPlayerPreLoginEvent.Result.ALLOWED);
       }
    }

    public User getLongestIdlePlayer() {
        List<User> idlePlayers = new ArrayList<>();
        essentials.getOnlineUsers().forEach(user -> {
            if (user.isAfk()) {
                idlePlayers.add(user);
            }
        });
        idlePlayers.sort(Comparator.comparingLong(User::getAfkSince));
        if(idlePlayers.isEmpty()){
            return null;
        }
        return idlePlayers.get(0);
    }
}
