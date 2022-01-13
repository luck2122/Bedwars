package de.luck212.bw.gamestates;

import de.luck212.bw.countdowns.LobbyCountDown;
import de.luck212.bw.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class LobbyState extends GameState{
	
	private Main plugin;
	public static final int MIN_PLAYERS = 2,
							MAX_PLAYERS = 4;
	
	private LobbyCountDown countdown;
	
	public LobbyState(Main plugin, GameStateManager gameStateManager) {
		this.plugin = plugin;
		countdown = new LobbyCountDown(gameStateManager, plugin);
	}
	
	public void updateScoreBoard() {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = board.registerNewObjective("abcde", "abcde");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("§Bedwars");
		objective.getScore(" ").setScore(3);
		objective.getScore("§7Spieler: §6§" + (plugin.getTeamBlue().size() + plugin.getTeamRed().size())).setScore(2);
		objective.getScore(" ").setScore(1);
		objective.getScore("§7Bis Start: §6" + countdown.getSeconds() + " Sekunden").setScore(0);
		for(Player current : Bukkit.getOnlinePlayers())
			current.setScoreboard(board);
	}

	@Override
	public void start() {
		countdown.startIdle();
		
	}

	@Override
	public void stop() {

		
	}

	public LobbyCountDown getCountdown() {
		return countdown;
	}
}
