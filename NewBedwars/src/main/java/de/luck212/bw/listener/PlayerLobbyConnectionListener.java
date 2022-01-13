package de.luck212.bw.listener;

import de.luck212.bw.countdowns.LobbyCountDown;
import de.luck212.bw.gamestates.LobbyState;
import de.luck212.bw.main.Main;
import de.luck212.bw.util.ConfigLocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerLobbyConnectionListener implements Listener {
    public static final String TEAM_MENU_ITEM_NAME = "§6§lTeam-Auswahl";

    Main plugin;
    ItemStack teamItem;

    public PlayerLobbyConnectionListener(Main plugin){
        this.plugin = plugin;
        teamItem = new ItemStack(Material.BED);
        ItemMeta itemMeta = teamItem.getItemMeta();
        itemMeta.setDisplayName(TEAM_MENU_ITEM_NAME);
    }

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event){
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
        Player player = event.getPlayer();
        plugin.getPlayers().add(player);
        event.setJoinMessage(Main.PREFIX + "§a" + player.getName() + " §7hat das Spiel betreten [" + plugin.getPlayers().size() + "/" + LobbyState.MAX_PLAYERS + "§7]");
        player.getInventory().clear();
        player.getInventory().setChestplate(null);
        player.getInventory().setHelmet(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().setItem(4,teamItem);
        for(Player current : Bukkit.getOnlinePlayers()) {
            current.showPlayer(player);
            player.showPlayer(current);
        }

        ConfigLocationUtil locationUtil = new ConfigLocationUtil(plugin, "Lobby");
        if(locationUtil.loadLocation() != null) {
            player.teleport(locationUtil.loadLocation());
        }else
            Bukkit.getConsoleSender().sendMessage("§cDie Lobby-Location wurde noch nicht gesetzt!");

        LobbyState lobbyState= (LobbyState)plugin.getGameStateManager().getCurrentGameState();
        LobbyCountDown countdown = lobbyState.getCountdown();
        if(plugin.getPlayers().size() >= LobbyState.MIN_PLAYERS) {
            if(!countdown.isRunning()) {
                countdown.stopIdle();
                countdown.start();
            }
        }
        lobbyState.updateScoreBoard();
    }

    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent event){
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)){
            Player player = event.getPlayer();
            event.setQuitMessage(Main.PREFIX + "§c" + player.getName() + "§7 hat das Spiel verlassen");
            return;
        }
        Player player = event.getPlayer();
        plugin.getPlayers().remove(player);
        event.setQuitMessage(Main.PREFIX + "§c" + player.getDisplayName() + " §7hat das Spiel verlassen. [" +
                plugin.getPlayers().size() + "/" + LobbyState.MAX_PLAYERS + "]" );
        LobbyState lobbyState= (LobbyState)plugin.getGameStateManager().getCurrentGameState();
        LobbyCountDown countdown = lobbyState.getCountdown();
        if(plugin.getPlayers().size() < LobbyState.MIN_PLAYERS) {
            if(countdown.isRunning()) {
                countdown.stop();
                countdown.startIdle();
            }
        }
        lobbyState.updateScoreBoard();
    }
}