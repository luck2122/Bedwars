package de.luck212.bw.countdowns;

import de.luck212.bw.gamestates.GameState;
import de.luck212.bw.gamestates.GameStateManager;
import de.luck212.bw.gamestates.IngameState;
import de.luck212.bw.gamestates.LobbyState;
import de.luck212.bw.main.Main;
import de.luck212.bw.maps.Map;
import de.luck212.bw.util.ConfigLocationUtil;
import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public class LobbyCountDown extends CountDown {

	private static final int COUNTDOWN_TIME = 60, IDLE_TIME = 15;

	private int seconds;
	private boolean isRunning;
	private int idleID;
	private boolean isIdling;
	private GameStateManager gameStateManager;
	private Map map;
	private Main plugin;

	public LobbyCountDown(GameStateManager gameStateManager, Main plugin) {
		this.gameStateManager = gameStateManager;
		this.plugin = plugin;
		seconds = COUNTDOWN_TIME;
	}

	@Override
	public void start() {
		isRunning = true;

		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(gameStateManager.getPlugin(), new Runnable() {

			@Override
			public void run() {
				switch (seconds) {
				case 60: case 45: case 30: case 15: case 3: case 2:
					Bukkit.broadcastMessage(Main.PREFIX + "§7Das Spiel startet in §a" + seconds + " Sekunden§7.");

					if (seconds == 3) {

						map = plugin.getWinnerMap();


						Bukkit.broadcastMessage(Main.PREFIX + "§7Es wird gespielt: §6" + map.getName());
						map.load();
						for (Player current : Bukkit.getOnlinePlayers())
							current.getInventory().clear();
					}

					PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE,
							ChatSerializer.a("{\"text\":\"" + map.getName() + "\", \"color\":\"green\"}"), 20,
							40, 20);
					PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE,
							ChatSerializer
									.a("{\"text\":\" Von: " + map.getBuilder() + "\", \"color\": \"yellow\"}"),
							20, 40, 20);

					for(Player player : Bukkit.getOnlinePlayers()){
						((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(title);
						((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(subtitle);
					}

					break;
					case 1:
						Bukkit.broadcastMessage(Main.PREFIX + "§7Das Spiel startet in §aeiner Sekunde§7.");
						break;
				case 0:
					gameStateManager.setGameState(GameState.INGAME_STATE);
					Location spawnerBlue = new ConfigLocationUtil(plugin, "Arenas." + map.getName() + ".Spawner.SpawnerBlue").loadblockLocation();
					Location spawnerRed = new ConfigLocationUtil(plugin, "Arenas." + map.getName() + ".Spawner.SpawnerRed").loadblockLocation();
					IngameState ingameState = (IngameState) gameStateManager.getCurrentGameState();

					ingameState.startDropCicle(spawnerBlue);
					ingameState.startDropCicle(spawnerRed);
					plugin.getTeamManager().setTeamMembers();

					stop();
					break;

				default:
					break;
				}
				if (gameStateManager.getCurrentGameState() instanceof LobbyState)
					((LobbyState) gameStateManager.getCurrentGameState()).updateScoreBoard();

				seconds--;

				for (Player current : Bukkit.getOnlinePlayers()) {
					current.setLevel(seconds);

					switch (seconds) {
					case 60: case 30: case 15: case 3: case 2:
					case 1:
						current.playSound(current.getLocation(), Sound.NOTE_BASS, 0.2f, 1.4f);
						break;

					default:
						break;
					}

				}
			}
		}, 0, 20);

	}

	@Override
	public void stop() {
		if(isRunning) {
			Bukkit.getScheduler().cancelTask(taskID);
			isRunning = false;
			seconds = COUNTDOWN_TIME;
			if(gameStateManager.getCurrentGameState() instanceof LobbyState)
				((LobbyState) gameStateManager.getCurrentGameState()).updateScoreBoard();
		}
	}

	public void startIdle() {
		isIdling = true;
		idleID = Bukkit.getScheduler().scheduleSyncRepeatingTask(gameStateManager.getPlugin(), new Runnable() {

			@Override
			public void run() {
				Bukkit.broadcastMessage(Main.PREFIX + "§7Bis zum Spielstart fehlen noch §6" +
						(LobbyState.MIN_PLAYERS - Bukkit.getOnlinePlayers().size()) +
						" Spieler§7.");

			}
		}, 0, 20 * IDLE_TIME);
	}

	public void stopIdle() {
		if(isIdling) {
			Bukkit.getScheduler().cancelTask(idleID);
			isIdling = false;
		}
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	public boolean isRunning() {
		return isRunning;
	}

}
