package de.luck212.bw.gamestates;

import de.luck212.bw.main.Main;
import de.luck212.bw.maps.Map;
import de.luck212.bw.resources.ResourceSpawner;
import de.luck212.bw.teams.Teams;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;

public class IngameState extends GameState{
	
	private Main plugin;
	private Map map;
	private ArrayList<Player> players, spectators;
	public boolean grace;
	private Teams winningTeam;

	public IngameState(Main plugin) {
		this.plugin = plugin;
		spectators = new ArrayList<Player>();
	}

	@Override
	public void start() {
		grace = true;
		map = plugin.getWinnerMap();
		map.load();
		checkForEqualTeams();
		for(Player player : plugin.getTeamBlue()){
			player.setLevel(0);
			player.setHealth(20);
			player.setFoodLevel(20);
			player.getInventory().clear();
			player.setGameMode(GameMode.SURVIVAL);
			player.teleport(map.getSpawnLocations()[0]);
			updateScoreboard(player);
		}
		for(Player player : plugin.getTeamRed()){
			player.setLevel(0);
			player.setHealth(20);
			player.setFoodLevel(20);
			player.getInventory().clear();
			player.setGameMode(GameMode.SURVIVAL);
			player.teleport(map.getSpawnLocations()[1]);
			updateScoreboard(player);
		}
	}
	
	public void updateScoreboard(Player player) {
		
		
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = board.registerNewObjective("abcd", "abcd");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("§6Bedwars");
		objective.getScore(" ").setScore(3);
			Teams teamRole = plugin.getTeamManager().getTeamRole(player);
			objective.getScore("§7Dein Team: " + teamRole.getChatColor() + teamRole.getName()).setScore(2);
			
		
		objective.getScore(" ").setScore(1);
		objective.getScore("§cTeam-Rot: §6" + plugin.getTeamRed().size()).setScore(0);
		objective.getScore("§9Team-Blau: §6" + plugin.getTeamBlue().size()).setScore(1);
		player.setScoreboard(board);
	}

	public void checkGameEnding() {
		if(plugin.getTeamBlue().size() <= 0) {
			winningTeam = Teams.TeamRED;
			plugin.getGameStateManager().setGameState(ENDING_STATE);
		}else if(plugin.getTeamRed().size() <=0);{
			winningTeam = Teams.TeamBLUE;
			plugin.getGameStateManager().setGameState(ENDING_STATE);
		}
	}

	public void addSpectator(Player player) {
		spectators.add(player);
		player.setGameMode(GameMode.CREATIVE);

		System.out.println(map.getName());
		map = plugin.getWinnerMap();
		map.load();
		player.teleport(map.getSpectatorLocation());

		for (Player current : Bukkit.getOnlinePlayers()) {
			current.hidePlayer(player);
		}
	}

	public void startDropCicle(Location dropLocation){
			ResourceSpawner resourceSpawner = new ResourceSpawner(plugin, dropLocation, dropLocation.getWorld());
			resourceSpawner.startDropCicle();
	}

	@Override
	public void stop() {
		Bukkit.broadcastMessage(Main.PREFIX + "§6 Sieger: " + winningTeam.getChatColor() + winningTeam.getName());
	}


	private void checkForEqualTeams(){
		if(plugin.getTeamBlue().size() == 2 && plugin.getTeamRed().size() == 0){
			Collections.shuffle(plugin.getTeamBlue());
			Player player = plugin.getTeamBlue().get(0);
			plugin.getTeamBlue().remove(player);
			plugin.getTeamRed().add(player);
		}else if (plugin.getTeamRed().size() == 2 && plugin.getTeamBlue().size() == 0){
			Collections.shuffle(plugin.getTeamRed());
			Player player = plugin.getTeamRed().get(0);
			plugin.getTeamRed().remove(player);
			plugin.getTeamBlue().add(player);
		}
	}

	public boolean isInGrace() {
		return grace;
	}

	public ArrayList<Player> getSpectators() {
		return spectators;
	}


}
