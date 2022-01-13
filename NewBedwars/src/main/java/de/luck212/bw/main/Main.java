package de.luck212.bw.main;

import de.luck212.bw.commands.SetupCommand;
import de.luck212.bw.commands.StartCommand;
import de.luck212.bw.gamestates.GameState;
import de.luck212.bw.gamestates.GameStateManager;
import de.luck212.bw.listener.GameProgressionListener;
import de.luck212.bw.listener.GameProtectionListener;
import de.luck212.bw.listener.PlayerLobbyConnectionListener;
import de.luck212.bw.listener.TeamListener;
import de.luck212.bw.maps.Map;
import de.luck212.bw.resources.ResourceSpawner;
import de.luck212.bw.teams.TeamManager;
import de.luck212.bw.villager.VillagerHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin{
    //TODO Doppelte Warnungen auslagern
    public static final String PREFIX = "§7[§1Bedwars§7] §r",
            PERMISSION = PREFIX + "§cDazu hast du keine Rechte!";

    private GameStateManager gameStateManager;
    private ArrayList<Player> teamBlue;
    private ArrayList<Player> teamRed;
    private GameProgressionListener gameProgressListener;
    private GameProtectionListener gameProtectionListener;
    private TeamManager teamManager;
    private Map winnerMap;
    private ResourceSpawner resourceSpawner;
    private VillagerHandler villagerHandler;
    private ArrayList<Player> players;
    @Override
    public void onEnable() {
        gameStateManager = new GameStateManager(this);
        System.out.println(PREFIX + "Der Server startet nun!");
        resourceSpawner = new ResourceSpawner();
        villagerHandler = new VillagerHandler(this);

        gameStateManager.setGameState(GameState.LOBBY_STATE);

        init(Bukkit.getPluginManager());
    }

    public void init(PluginManager pluginManager){
        teamManager = new TeamManager(this);
        gameProtectionListener = new GameProtectionListener(this);

        getCommand("setup").setExecutor(new SetupCommand(this));
        getCommand("shopcreate").setExecutor(villagerHandler);
        getCommand("start").setExecutor(new StartCommand(this));
        pluginManager.registerEvents(new GameProgressionListener(this, winnerMap), this);
        pluginManager.registerEvents(new PlayerLobbyConnectionListener(this), this);
        pluginManager.registerEvents(new TeamListener(this), this);
        pluginManager.registerEvents(gameProtectionListener, this);

        for(String current : getConfig().getConfigurationSection("Arenas.").getKeys(false)) {
            Map map = new Map(this, current);
            if(map.playable()) {
                this.winnerMap = map;
            }else
                Bukkit.getConsoleSender().sendMessage("§cDie Map §4" + map.getName() + "§c ist noch nicht fertig eingerichtet.");
        }
    }

    @Override
    public void onDisable() {

    }

    public GameStateManager getGameStateManager() {
        return gameStateManager;
    }

    public ArrayList<Player> getTeamBlue() {
        return teamBlue;
    }

    public ArrayList<Player> getTeamRed() {
        return teamRed;
    }

    public GameProgressionListener getGameProgressionListener() {
        return gameProgressListener;
    }

    public GameProtectionListener getGameProtectionListener() {
        return gameProtectionListener;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public void setWinnerMap(Map winnerMap) {
        this.winnerMap = winnerMap;
    }

    public Map getWinnerMap() {
        return winnerMap;
    }

    public ResourceSpawner getResourceSpawner() {
        return resourceSpawner;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }
}
