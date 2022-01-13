package de.luck212.bw.countdowns;

import de.luck212.bw.main.Main;
import de.luck212.bw.util.ConfigLocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EndigCountDown extends CountDown{
    private static final int ENDING_SECONDS = 15;

    private Main plugin;
    private int seconds;


    public EndigCountDown(Main plugin) {
        this.plugin = plugin;
        seconds = ENDING_SECONDS;
    }

    @Override
    public void start() {
        ConfigLocationUtil configLocationUtil = new ConfigLocationUtil(plugin, "Lobby");

        for(Player player : Bukkit.getOnlinePlayers()){
            if(configLocationUtil.loadLocation() != null)
                return;
            player.getInventory().clear();
            player.teleport(configLocationUtil.loadLocation());
        }

        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            @Override
            public void run() {
                switch (seconds){
                    case 15: case 10: case 5: case 4: case 3: case 2:
                        Bukkit.broadcastMessage(Main.PREFIX  + "§7Der Server stoppt in §a" + seconds + "§7 Sekunden.");
                        break;
                    case 1:
                        Bukkit.broadcastMessage(Main.PREFIX + "§7Der Server stoppt in §a" + seconds + "§7 Sekunde.");
                        break;
                    case 0:
                        Bukkit.broadcastMessage(Main.PREFIX  + "§cDer Server stoppt nun.");
                        plugin.getGameStateManager().getCurrentGameState().stop();
                        stop();
                        break;
                    default:
                        break;
                }
            }
        }, 0, 20);

    }

    @Override
    public void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
        for(Player player : Bukkit.getOnlinePlayers())
            player.kickPlayer("§cDas Spiel ist zu ende.");
    }
}