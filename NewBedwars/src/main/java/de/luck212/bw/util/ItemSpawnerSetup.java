package de.luck212.bw.util;

import de.luck212.bw.main.Main;
import de.luck212.bw.maps.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ItemSpawnerSetup implements Listener {

    Main plugin;
    private Player player;
    private Map map;
    private int phase;
    private boolean finished;
    private Location blockLocation;
    private Block itemSpawnerBlue;
    private Block itemSpawnerRed;

    public ItemSpawnerSetup(Main plugin, Player player, Map map){
        this.plugin = plugin;
        this.player = player;
        this.map = map;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        phase = 1;
        finished = false;

        startSetup();
    }

    public void startSetup(){
        player.sendMessage(Main.PREFIX + "§aDu hast ein Item-Spawner Setup gestartet.");
        player.sendMessage(Main.PREFIX + "§6Starte mit Schritt 1");
        startPhase(phase);
    }

    public void startPhase(int phase){
        switch (phase){
            case 1:
                player.sendMessage(Main.PREFIX + "§aKlicke den Blauen Item-Spawner an.");
                break;
            case 2:
                player.sendMessage(Main.PREFIX + "§aKlicke den Roten Item-Spawner an.");
                break;
        }
    }

    @EventHandler
    public void handleBlockBreak(BlockBreakEvent event){
        if(!event.getPlayer().getName().equals(player.getName())) return;
        if(finished) return;
        event.setCancelled(true);
        if(phase == 1) {
            if(event.getBlock().getType() != Material.STAINED_CLAY ) return;
            itemSpawnerBlue = event.getBlock();
        }else if(phase == 2) {
            if(event.getBlock().getType() != Material.BED || event.getBlock().getType() != Material.BED_BLOCK) return;
            itemSpawnerRed = event.getBlock();
            finishSetup();
        }
    }

    public void finishSetup() {
        player.sendMessage(Main.PREFIX + "§aDas Setup wurde abgeschlossen.");
        finished = true;


        new ConfigLocationUtil(plugin, itemSpawnerBlue.getLocation(), "Arenas." + map.getName() + ".Spawner.SpawnerBlue").saveBlockLocation();;
        new ConfigLocationUtil(plugin, itemSpawnerRed.getLocation(), "Arenas." + map.getName() + ".Spawner.SpawnerRed").saveBlockLocation();;
    }
}