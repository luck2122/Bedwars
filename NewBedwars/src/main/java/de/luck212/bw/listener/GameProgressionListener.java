package de.luck212.bw.listener;

import de.luck212.bw.gamestates.EndState;
import de.luck212.bw.gamestates.GameState;
import de.luck212.bw.gamestates.IngameState;
import de.luck212.bw.main.Main;
import de.luck212.bw.maps.Map;
import de.luck212.bw.teams.TeamManager;
import de.luck212.bw.teams.Teams;
import de.luck212.bw.util.ConfigLocationUtil;
import net.minecraft.server.v1_8_R1.EnumClientCommand;
import net.minecraft.server.v1_8_R1.PacketPlayInClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameProgressionListener implements Listener {
	
	private Main plugin;
	private TeamManager teamManager;
	private Map map;
	private boolean bedBlueDestroyed = false;
	private boolean bedRedDestroyed = false;
	private Location bedBlueLocation;
	private Location bedRedLocation;
	private Location spawnBlue;
	private Location spawnRed;

	public GameProgressionListener(Main plugin, Map map) {
		this.plugin = plugin;
		teamManager = plugin.getTeamManager();
		if(map != null){
			bedBlueLocation = new ConfigLocationUtil(plugin, "Arenas." + map.getName() + ".Beds.BedBlue").loadblockLocation();
			bedRedLocation = new ConfigLocationUtil(plugin, "Arenas." + map.getName() + ".Beds.BedRed").loadblockLocation();
			spawnBlue = new ConfigLocationUtil(plugin, "Arenas." + map.getName() + ".1").loadLocation();
			spawnRed = new ConfigLocationUtil(plugin, "Arenas." + map.getName() + ".2").loadLocation();
		}
	}
	

	@EventHandler
	public void handlePlayerDamage(EntityDamageByEntityEvent event) {
		if (!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState))
			return;
		if (!(event.getDamager() instanceof Player))
			return;
		if (!(event.getEntity() instanceof Player))
			return;
		Player damager = (Player) event.getDamager(), victim = (Player) event.getEntity();
		Teams damagerTeam = teamManager.getTeamRole(damager);
		Teams victimTeam = teamManager.getTeamRole(victim);
		if (damagerTeam == null && victimTeam == null)
			event.setCancelled(true);
	}


	@EventHandler(priority = EventPriority.HIGH)
	public void handlePlayerDeathEvent(PlayerDeathEvent event) {
		if (!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState))
			return;
			
		event.setDeathMessage(null);
		IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
		Player victim = event.getEntity();
		victim.getInventory().clear();
		event.getDrops().clear();
		if (victim.getKiller() != null) {

			Player killer = victim.getKiller();
			Teams killerTeam = teamManager.getTeamRole(killer), victimTeam = teamManager.getTeamRole(victim);

			switch (killerTeam) {
				case TeamBLUE:
				if (victimTeam == Teams.TeamBLUE) {
					return;
				} else if(victimTeam == Teams.TeamRED){
					event.setDeathMessage(
							Main.PREFIX + killerTeam.getChatColor() + killer.getName() + " hat " + victimTeam.getChatColor() + victim.getName() + " §agetötet.");
				}
				break;
				case TeamRED:
				if (victimTeam == Teams.TeamRED) {
					return;
				} else if (victimTeam == Teams.TeamBLUE) {
					event.setDeathMessage(
							Main.PREFIX + killerTeam.getChatColor() + killer.getName() + " hat " + victimTeam.getChatColor() + victim.getName() + " §agetötet.");
				}
			break;
				default:
					break;
			}

			victim.sendMessage(
					Main.PREFIX + "§7Du wurdest von " + killerTeam.getChatColor() + killer.getName() + " §7 getötet.");

			checkDeath(victimTeam, victim, ingameState);
			ingameState.checkGameEnding();
			
		} else {
			Teams victimTeam = teamManager.getTeamRole(victim);
			event.setDeathMessage(Main.PREFIX + victimTeam.getChatColor() + victim.getName() + " §aist gestorben.");
			checkDeath(victimTeam, victim, ingameState);

			ingameState.checkGameEnding();
		}
		for(Player current : Bukkit.getOnlinePlayers()) {
			ingameState.updateScoreboard(current);
		}
	}

	@EventHandler
	public void handlePlayerQuit(PlayerQuitEvent event) {
		if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
			if(plugin.getGameStateManager().getCurrentGameState() instanceof EndState) return;
			Player player = event.getPlayer();
			if(plugin.getTeamRed().contains(player))
				plugin.getTeamRed().remove(player);
			else if(plugin.getTeamBlue().contains(player))
				plugin.getTeamBlue().remove(player);
			event.setQuitMessage(Main.PREFIX + "§7Der spieler §6"+ player.getName() + "§7 hat das Spiel verlassen.");
		}
		Player player = event.getPlayer();
		Teams playerTeam = teamManager.getTeamRole(player);
		IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();

		if(plugin.getTeamBlue().contains(player)){
			plugin.getTeamBlue().remove(player);
			event.setQuitMessage(Main.PREFIX + "§7Der spieler " + playerTeam.getChatColor() + player.getName() + "§7 hat das Spiel verlassen.");
			ingameState.checkGameEnding();
		}else if(plugin.getTeamRed().contains(player)){
			plugin.getTeamRed().remove(player);
			event.setQuitMessage(Main.PREFIX + "§7Der spieler " + playerTeam.getChatColor() + player.getName() + "§7 hat das Spiel verlassen.");
			ingameState.checkGameEnding();
		}
	}

	@EventHandler
	public void handleBedDestruction(BlockBreakEvent event){
		if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
		Player player = event.getPlayer();
		Teams playerTeam = plugin.getTeamManager().getTeamRole(player);
		Block block = event.getBlock();

		if(block.getType() != Material.BED || block.getType() != Material.BED_BLOCK){
			switch (block.getType()){
				case SANDSTONE:	case IRON_BLOCK: case LADDER:
					return;
				default:
					event.setCancelled(true);
					player.sendMessage(Main.PREFIX + "§cDu kannst kein " + block.getType() + " abbauen");
					return;
			}
		}else{
			if(playerTeam == Teams.TeamBLUE && bedBlueLocation == block.getLocation()){
				event.setCancelled(true);
				player.sendMessage(Main.PREFIX + "§cDu kannst dein eigenes Bett nicht abbauen!");
			}else if(playerTeam == Teams.TeamBLUE && bedRedLocation == block.getLocation()){
				for(Player current : Bukkit.getOnlinePlayers()){
					current.sendMessage(Main.PREFIX + "§7Das Bett von §cTeam-Rot §7wurde von " + playerTeam.getChatColor() + player.getName() + " §7abgebaut.");
					current.playSound(current.getLocation(), Sound.WITHER_DEATH, 02.f, 1.4f);
				}
				bedRedDestroyed = true;
			}else if(playerTeam == Teams.TeamRED && bedRedLocation == block.getLocation()){
				event.setCancelled(true);
				player.sendMessage(Main.PREFIX + "§cDu kannst dein eigenes Bett nicht abbauen!");
			}else if(playerTeam == Teams.TeamRED && bedBlueLocation == block.getLocation()){
				for(Player current : Bukkit.getOnlinePlayers()){
					current.sendMessage(Main.PREFIX + "§7Das Bett von §cTeam-Rot §7wurde von " + playerTeam.getChatColor() + player.getName() + " §7abgebaut.");
					current.playSound(current.getLocation(), Sound.WITHER_DEATH, 02.f, 1.4f);
				}
				bedBlueDestroyed = true;
			}
		}
	}

	private void checkDeath(Teams victimTeam, Player victim, IngameState ingameState){

		if(victimTeam == Teams.TeamRED && bedRedDestroyed){
			plugin.getTeamRed().remove(victim);
			ingameState.addSpectator(victim);
		}else if(!bedRedDestroyed){
			PacketPlayInClientCommand packet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
			((CraftPlayer)victim).getHandle().playerConnection.a(packet);
			victim.teleport(spawnRed);
		}

		if(victimTeam == Teams.TeamRED && bedRedDestroyed){
			plugin.getTeamRed().remove(victim);
			ingameState.addSpectator(victim);
		}else if(!bedRedDestroyed){
			PacketPlayInClientCommand packet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
			((CraftPlayer)victim).getHandle().playerConnection.a(packet);
			victim.teleport(spawnRed);
		}
	}

}
