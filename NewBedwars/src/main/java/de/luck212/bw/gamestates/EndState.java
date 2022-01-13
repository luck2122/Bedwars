package de.luck212.bw.gamestates;

import de.luck212.bw.countdowns.EndigCountDown;
import de.luck212.bw.main.Main;
import de.luck212.bw.maps.Map;
import de.luck212.bw.util.ConfigLocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class EndState extends GameState{
	
	private Main plugin;
	private EndigCountDown endigCountDown;
	private Map map;


	public EndState(Main plugin) {
		this.plugin = plugin;
		map = plugin.getWinnerMap();
		if(map != null)
			map.load();
		endigCountDown = new EndigCountDown(this.plugin);
		if(plugin.getResourceSpawner() != null)
			plugin.getResourceSpawner().cancelAllDropCicles();
	}

	@Override
	public void start() {
		endigCountDown.start();

		ConfigLocationUtil locationUtil = new ConfigLocationUtil(plugin, "Lobby");

		for(Player player : Bukkit.getOnlinePlayers()){
			player.setLevel(0);
			player.setHealth(20);
			player.setFoodLevel(20);
			player.getInventory().clear();
			player.setGameMode(GameMode.SURVIVAL);
			if(locationUtil.loadLocation() != null) {
				player.teleport(locationUtil.loadLocation());
			}else
				Bukkit.getConsoleSender().sendMessage("§cDie Lobby-Location wurde noch nicht gesetzt!");
		}
	}

	@Override
	public void stop() {
		
	}

}
