package de.luck212.bw.maps;

import de.luck212.bw.gamestates.LobbyState;
import de.luck212.bw.main.Main;
import de.luck212.bw.util.ConfigLocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;


public class Map {
	
	private Main plugin;
	private String name;
	private String builder;
	private Location[] spawnLocations = new Location[LobbyState.MAX_PLAYERS];
	private Location spectatorLocation;
	
	public Map(Main plugin, String name) {
		this.plugin = plugin;
		this.name = name;
		
		if(exists())
			builder = plugin.getConfig().getString("Arenas." + name + ".Builder");
	}
	
	public boolean exists() {
		return (plugin.getConfig().getString("Arenas." + name + ".Builder") != null);
	}
	
	public void create(String builder) {
		this.builder = builder;
		plugin.getConfig().set("Arenas." + name + ".Builder", builder);
		plugin.saveConfig();
		
	}

	public boolean playable() {
		ConfigurationSection configSection = plugin.getConfig().getConfigurationSection("Arenas." + name);
		if(!configSection.contains("Spectator")) return false;
		if(!configSection.contains("Builder")) return false;
		if(!configSection.contains("spawnBlue")) return false;
		if(!configSection.contains("spawnRed")) return false;
		return true;
	}

	public void load() {
		for(int i = 0; i < spawnLocations.length; i++) {
			spawnLocations[i] = new ConfigLocationUtil(plugin, "Arenas." + name + "." + (i + 1)).loadLocation();
		}
		spawnLocations[0] = new ConfigLocationUtil(plugin, "Arenas." + name + ".spawnBlue").loadLocation();
		spawnLocations[1] = new ConfigLocationUtil(plugin, "Arenas." + name + ".spawnRed").loadLocation();
		spectatorLocation = new ConfigLocationUtil(plugin, "Arenas." + name + ".Spectator").loadLocation();
	}

	public void setSpawnLocation(int spawnNumber, Location location) {
		if(spawnNumber == 1)
			new ConfigLocationUtil(plugin, location, "Arenas." + name + ".spawnBlue").saveLocation();
		else if(spawnNumber == 2)
			new ConfigLocationUtil(plugin, location, "Arenas." + name + ".spawnRed").saveLocation();
	}

	public void setSpectatorLocation(Location location) {
		spectatorLocation = location;
		new ConfigLocationUtil(plugin, location, "Arenas." + name + ".Spectator").saveLocation();
	}
	
	public String getBuilder() {
		return builder;
	}
	
	public String getName() {
		return name;
	}
	
	public Location[] getSpawnLocations() {
		return spawnLocations;
	}
	
	public Location getSpectatorLocation() {
		return spectatorLocation;
	}
	
	
}
