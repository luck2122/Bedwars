package de.luck212.bw.listener;

import de.luck212.bw.gamestates.IngameState;
import de.luck212.bw.main.Main;
import net.minecraft.server.v1_8_R1.EnumClientCommand;
import net.minecraft.server.v1_8_R1.PacketPlayInClientCommand;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBedEnterEvent;


public class GameProtectionListener implements Listener{
	
	private Main plugin;
	
	public GameProtectionListener(Main plugin) {
		this.plugin = plugin;
	}
	//TODO test test
	@EventHandler
	public void handleCreatureSpawn(CreatureSpawnEvent event) {
		if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void handleBedEnter(PlayerBedEnterEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void handleBowShot(EntityShootBowEvent event) {
		if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
			event.setCancelled(true);
			return;
		}
		
		IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
		if(ingameState.isInGrace())
			event.setCancelled(true);
	}
	
	@EventHandler
	public void handleEntityDamageEntity(EntityDamageByEntityEvent event) {
		if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
			event.setCancelled(true);
			return;
		}
		
		IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
		if(ingameState.isInGrace())
			event.setCancelled(true);
		
		if(!(event.getDamager() instanceof Player)) return;
		Player player = (Player) event.getDamager();
		if(ingameState.getSpectators().contains(player))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void handlePlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		PacketPlayInClientCommand packet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
		((CraftPlayer)player).getHandle().playerConnection.a(packet); 
		
		event.getDrops().clear();
		player.getInventory().setChestplate(null);
		player.getInventory().setHelmet(null);
		

		
		if(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState) {
			IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
			//ingameState.addSpectator(player);
		}else
			player.setGameMode(GameMode.CREATIVE);
		
	}
	
	
	@EventHandler
	public void handleExplosionDestruction(EntityExplodeEvent event) {
		event.blockList().clear();
	}
	

}
