package de.luck212.bw.listener;

import de.luck212.bw.gamestates.LobbyState;
import de.luck212.bw.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class TeamListener implements Listener {
    public static final String TEAM_BLUE_BED = "§9Team-Blau",
                                TEAM_RED_BED = "§cTeam-Rot";

    Main plugin;
    ArrayList<String> teamBlueLore;
    ArrayList<String> teamRedLore;

    public TeamListener(Main plugin){
        this.plugin = plugin;
        teamBlueLore = new ArrayList<String>();
        teamRedLore = new ArrayList<String>();
    }

    @EventHandler
    public void handleTeamMenuOpener(PlayerInteractEvent event){
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
        if(event.getAction() != Action.RIGHT_CLICK_AIR) return;
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if(item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) return;
        if(item.getItemMeta().getDisplayName().equals(PlayerLobbyConnectionListener.TEAM_MENU_ITEM_NAME)){
            Inventory teamInventory = Bukkit.createInventory(null, 9*1, "§6Teams");
            if(!(plugin.getTeamRed().size() >= 0)){
                for(int i = 0; i < plugin.getTeamRed().size(); i++){
                    Player current = plugin.getTeamRed().get(i);
                    teamRedLore.add(">> " + current.getName());
                }
            }
            if(!(plugin.getTeamBlue().size() >= 0)){
                for(int i = 0; i < plugin.getTeamBlue().size(); i++){
                    Player current = plugin.getTeamBlue().get(i);
                    teamRedLore.add(">> " + current.getName());
                }
            }

            ItemStack bedBlue = new ItemStack(Material.BED);
            ItemMeta bedBlueMeta = bedBlue.getItemMeta();
            bedBlueMeta.setDisplayName(TEAM_BLUE_BED);
            bedBlueMeta.setLore(teamBlueLore);
            bedBlue.setItemMeta(bedBlueMeta);

            ItemStack bedRed = new ItemStack(Material.BED);
            ItemMeta bedRedMeta = bedRed.getItemMeta();
            bedRedMeta.setDisplayName(TEAM_RED_BED);
            bedRedMeta.setLore(teamRedLore);
            bedRed.setItemMeta(bedRedMeta);

            teamInventory.setItem(4, bedBlue);
            teamInventory.setItem(8, bedRed);
            player.openInventory(teamInventory);
        }
    }

    @EventHandler
    public void handlePlayerClickTeam(InventoryClickEvent event){
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
        if(!(event.getClickedInventory().getName().equals("§8Teams"))) return;
        if(!(event.getCurrentItem().getType() == Material.BED)) return;
        if(!(event.getWhoClicked() instanceof  Player)) return;
        Player player = (Player) event.getWhoClicked();

        ItemStack clickedItem = event.getCurrentItem();
        ItemMeta clickedItemMeta = clickedItem.getItemMeta();

        if(clickedItemMeta.getDisplayName() == null || clickedItemMeta.getLore() == null) return;
        if(clickedItemMeta.getDisplayName().equals(TEAM_BLUE_BED)){
            if(plugin.getTeamBlue().size() >= 2){
                player.sendMessage(Main.PREFIX + "§cDas Team ist bereits voll.");
                return;
            }
            if(plugin.getTeamBlue().contains(player)){
                plugin.getTeamBlue().remove(player);
                player.setDisplayName("§r" + player.getName());
                player.setPlayerListName("§r" + player.getName());
            }else{
                player.setDisplayName("§9" + player.getName());
                player.setPlayerListName("§9" + player.getName());
                plugin.getTeamBlue().add(player);
            }

            return;
        }else if(clickedItemMeta.getDisplayName().equals(TEAM_RED_BED)){
            if(plugin.getTeamRed().size() >= 2){
                player.sendMessage(Main.PREFIX + "§cDas Team ist bereits voll.");
                return;
            }
            if(plugin.getTeamRed().contains(player)){
                plugin.getTeamBlue().remove(player);
                player.setDisplayName("§r" + player.getName());
                player.setPlayerListName("§r" + player.getName());

            }else {
                player.setDisplayName("§c" + player.getName());
                player.setPlayerListName("§c" + player.getName());
                plugin.getTeamRed().add(player);
            }

            return;
        }
    }
}