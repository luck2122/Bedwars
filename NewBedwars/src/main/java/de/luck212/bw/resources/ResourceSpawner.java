package de.luck212.bw.resources;

import de.luck212.bw.main.Main;
import net.minecraft.server.v1_8_R1.Items;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ResourceSpawner {

    Main plugin;
    private Location dropLocation;
    private World world;
    ItemStack bronze = null;
    ItemStack gold = null;
    ItemStack eisen = null;
    private int taskBronze = 0;
    private int taskSilver = 0;
    private int taskGold = 0;

    public ResourceSpawner(Main plugin, Location dropLocation, World world) {
        this.plugin = plugin;
        this.dropLocation = dropLocation;
        this.world = world;

        bronze = new ItemStack(Material.CLAY_BRICK);
        ItemMeta bronzeMeta = bronze.getItemMeta();
        bronzeMeta.setDisplayName("§fBronze");
        bronze.setItemMeta(bronzeMeta);

        eisen = new ItemStack(Material.IRON_INGOT);
        ItemMeta eisenMeta = eisen.getItemMeta();
        eisenMeta.setDisplayName("§fEisen");
        eisen.setItemMeta(eisenMeta);

        gold = new ItemStack(Material.GOLD_INGOT);
        ItemMeta goldMeta = gold.getItemMeta();
        goldMeta.setDisplayName("§eGold");
        gold.setItemMeta(goldMeta);
    }

    public ResourceSpawner() {

    }

    public void startDropCicle(){

        taskBronze = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                world.dropItem(dropLocation, bronze);
            }
        },20,20);


        taskSilver = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                world.dropItem(dropLocation, eisen);
            }
        },20, 200);
        taskGold = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                world.dropItem(dropLocation, gold);
            }
        }, 20, 600);
    }

    public void cancelAllDropCicles(){
        if(taskGold == 0 || taskSilver == 0 || taskBronze == 0)
            return;
        Bukkit.getScheduler().cancelTask(taskBronze);
        Bukkit.getScheduler().cancelTask(taskSilver);
        Bukkit.getScheduler().cancelTask(taskGold);
    }


}