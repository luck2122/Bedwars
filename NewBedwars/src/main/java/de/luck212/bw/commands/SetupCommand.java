package de.luck212.bw.commands;

import de.luck212.bw.gamestates.LobbyState;
import de.luck212.bw.main.Main;
import de.luck212.bw.maps.Map;
import de.luck212.bw.util.BedSetup;
import de.luck212.bw.util.ConfigLocationUtil;
import de.luck212.bw.util.ItemSpawnerSetup;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand implements CommandExecutor{
	
	private Main plugin;
	
	public SetupCommand(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.hasPermission("bw.setup")) {
			player.sendMessage(Main.PERMISSION);
		}
		
		if(args.length == 0) {
			player.sendMessage(Main.PREFIX + "§cBitte benutze /setup <LOBBY>");
		}else {
			if(args[0].equalsIgnoreCase("lobby")) {
				if(args.length == 1) {
				new ConfigLocationUtil(plugin, player.getLocation(), "Lobby").saveLocation();
				player.sendMessage(Main.PREFIX + "§aDie Lobby-Location wurde neu gesetzt.");
				}else 
				player.sendMessage(Main.PREFIX + "§cBitte benutze §6/setup <LOBBY>§c!");
			}else if(args[0].equalsIgnoreCase("create")) {
				if(args.length == 3) {
					Map map = new Map(plugin, args[1]);
					if(!map.exists()) {
						map.create(args[2]);
						player.sendMessage(Main.PREFIX + "§aDie Map §b" + map.getName() + " §awurde erstellt");
					}else
						player.sendMessage(Main.PREFIX + "§cDiese Map existiert bereits.");
				} else
					player.sendMessage(Main.PREFIX + "§cBitte benutze §6/setup create <NAME> <ERBAUER>§c!");
			}else if(args[0].equalsIgnoreCase("set")) {
				if(args.length == 3) {
					Map map = new Map(plugin, args[1]);
					if(map.exists()) {
						try {
						int spawnNumber = Integer.parseInt(args[2]);
						if(spawnNumber > 0 && spawnNumber <= LobbyState.MAX_PLAYERS) {
							map.setSpawnLocation(spawnNumber, player.getLocation());
							player.sendMessage(Main.PREFIX + "§aDu hast die Spawn-Location §6" + spawnNumber + "§a für die Map §6" + map.getName() + "§a gesetzt.");
						}else
							player.sendMessage(Main.PREFIX + "§cBitte gib eine Zahl zwischen §61 und " + LobbyState.MAX_PLAYERS + " §can");
						} catch(NumberFormatException e) {
							if(args[2].equalsIgnoreCase("spectator")) {
								map.setSpectatorLocation(player.getLocation());
								player.sendMessage(Main.PREFIX + "§aDu hast die §6Spectator-Location §afür die Map §6" + map.getName() + " §agesetzt.");
							}else
								player.sendMessage(Main.PREFIX + "§cBitte benutze §6/setup set <NAME> <1-" + LobbyState.MAX_PLAYERS + " // SPECTATOR>");
						}
					}else
						player.sendMessage(Main.PREFIX + "§cDiese Map existiert noch nicht.");
				}else
					player.sendMessage(Main.PREFIX + "§cBitte benutze §6/setup set <NAME> <1-" + LobbyState.MAX_PLAYERS + " // Specatator>");
			} else if(args[0].equalsIgnoreCase("itemspawner")){
				if(args.length == 1){
					Map map = new Map(plugin, args[1]);
					if(map.exists()){
						new ItemSpawnerSetup(plugin, player, map);
					}else
						player.sendMessage(Main.PREFIX + "§cDiese Map existiert noch nicht!");
				}else
					player.sendMessage(Main.PREFIX + "§cBitte benutze §6/setup itemspawn <NAME>");
			} else if(args[0].equalsIgnoreCase("bedsetup")){
				if(args.length == 1){
					Map map = new Map(plugin, args[1]);
					if(map.exists()){
						new BedSetup(plugin, player, map);
					}else
						player.sendMessage(Main.PREFIX + "§cDiese Map existiert noch nicht");
				}else
					player.sendMessage(Main.PREFIX + "§cBitte benutze §6/setup bedsetup");
			}
		} 
		
		return false;
	}
	
	

}
