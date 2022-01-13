package de.luck212.bw.util;

import de.luck212.bw.main.Main;
import de.luck212.bw.maps.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.Bed;

public class BedSetup implements Listener{
	

	private Main plugin;
	private Player player;
	private Map map;
	private int phase;
	private boolean finished;
	private Bed bedBlue;
	private Bed bedRed;
	
	public BedSetup(Main plugin, Player player, Map map ) {
		this.plugin = plugin;
		this.player = player;
		this.map = map;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
		phase = 1;
		finished = false;
		
		
		startSetup();
	}
	
	public void startPhase(int phase) {
		switch(phase) {
		case 1:
			player.sendMessage(Main.PREFIX + "§aBitte klicke das Bett von Team §9§lBlau §aan");
			phase++;
			break;
		case 2:
			player.sendMessage(Main.PREFIX + "§aBitte klicke das Bett von Team §c§lRot §aan");
			phase++;
			break;
			
			default:
				break;
		}
	}
	
	@EventHandler
	public void handleBedBreak(BlockBreakEvent event) {
		if(!event.getPlayer().getName().equals(player.getName())) return;
		if(finished) return;
		event.setCancelled(true);
		if(phase == 1) {
			if(event.getBlock().getType() != Material.BED || event.getBlock().getType() != Material.BED_BLOCK) return;
			bedBlue = (Bed) event.getBlock();
		}else if(phase == 2) {
			if(event.getBlock().getType() != Material.BED || event.getBlock().getType() != Material.BED_BLOCK) return;
			bedRed = (Bed) event.getBlock();
			finishSetup();
		}
	}
	
	public void startSetup() {
		player.sendMessage(Main.PREFIX + "§aDu hast ein Bed-Setup gestartet.");
		player.sendMessage(Main.PREFIX + "§6Starte mit Schritt 1");
		startPhase(phase);
	}
	
	public void finishSetup() {
		player.sendMessage(Main.PREFIX + "§aDas Setup wurde abgeschlossen.");
		finished = true;
		
		
		new ConfigLocationUtil(plugin, (((Block) bedBlue).getRelative(bedBlue.getFacing())).getLocation(), "Arenas." + map.getName() + ".Beds.BedBlue").saveBlockLocation();;
		new ConfigLocationUtil(plugin, (((Block) bedRed).getRelative(bedRed.getFacing())).getLocation(), "Arenas." + map.getName() + ".Beds.BedRed").saveBlockLocation();;
	}
	

}
