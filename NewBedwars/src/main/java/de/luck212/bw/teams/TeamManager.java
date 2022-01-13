package de.luck212.bw.teams;

import de.luck212.bw.main.Main;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class TeamManager {

    private Main plugin;
    private HashMap<String, Teams> playerTeams;
    private ArrayList<Player> players;
    public TeamManager(Main plugin){
        this.plugin = plugin;
        playerTeams = new HashMap<>();
        players = plugin.getPlayers();
    }

    public void setTeamMembers(){
        for(Player current : plugin.getTeamRed())
            playerTeams.put(current.getName(), Teams.TeamRED);

        for(Player current : plugin.getTeamBlue())
            playerTeams.put(current.getName(), Teams.TeamBLUE);

    }

    public Teams getTeamRole(Player player) {
        return playerTeams.get(player.getName());
    }
}